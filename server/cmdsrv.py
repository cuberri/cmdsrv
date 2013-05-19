#!/usr/bin/env python

#
# Author:: Christophe Uberri <cuberri@gmail.com>
#
# Copyright 2013, Christophe Uberri <cuberri@gmail.com>
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# ------------------------------------------------------------------------------
# 'Command server' aka cmdsrv is a tool used to invoke some commands to be 
# processed on the underlying system. It basically starts a simple http server 
# which listens for incoming requests containing the commands to process. 
# (See the api documentation for details)
#
# The motivation for this tool is due to issues with 1.6 jvm which fork+exec
# a new process for every supplied command. The memory of the child process is 
# thus copied from the parent. If the parent process (the jvm instance) uses a 
# huge amount of memory at the time the process is forked, the jvm exposes
# itself to a 'Cannot allocate memory' error.
#
# Other solutions (but discarded for infrastructure related concerns) are :
#   - tweek /proc/sys/vm/overcommit_memory to allow more memory allocation that
#     actually exists on the system
#   - java 8 which proposes a 'spawn' mechanism istead of the current 'fork+exec'
#     one
#
# A command server over HTTP may seem an 'overkill' solution, but :
#   - a java based solution is out de facto
#   - a socket based or zeromq based server was considered but performance was
#     not an issue as I wrote this piece of code
#   - simplicity above all : bottle is a hell of a simple python web server
#   - testability improved via human readable json payload (you can curl the
#     server easily)
#   - I'm not (yet :)) an experienced Python developer
#   - I like HTTP and I like REST
# ------------------------------------------------------------------------------

import sys
import subprocess
import ConfigParser
import logging
import json
from bottle import route, get, post, request, run, abort, error, HTTPResponse, HTTP_CODES

# ------------------------------------------------------------------------------
# CONST
# ------------------------------------------------------------------------------
VERSION = ""
CHANGESET = ""
CHANGESETDATE = ""

# ------------------------------------------------------------------------------
# API
# ------------------------------------------------------------------------------

@get('/status')
def callstatus():
    """Simple health check. Can be used for smoke tests for ex.
    """
    return "online"

@get('/version')
def callversion():
    """Simple health check. Can be used for smoke tests for ex.
    """
    return 'version: {0} changeSet:{1} changeSetDate: {2}'.format(VERSION, CHANGESET, CHANGESETDATE)

@post(path='/cmd')
def callcmd():
    """Execute a command

      - Parse the request body as json
      - Execute the given extracted command
      - Return a JSON object describing the result

    Example :
    ---------

    - Request :

    $ curl -XPOST "http://localhost:8055/cmd" -H "Content-Type: application/json" -d '{"cmd":["unlink", "/tmp/mylink"]}'

    - Response :
    {
        "cmd":["unlink", "/tmp/mylink"]
        "stdout":"None",
        "stderr":"None",
        "retval":0
    }

    """
    logging.debug('Content-Length:%s' % request.headers.get('Content-Length'))

    if request.headers.get("Content-Type") != "application/json":
        return errorhttpresponse(400, 'I only eat application/json requests mate')

    # get request data
    reqdata = request.body.read()
    logging.debug('Got data from request : %s', reqdata)
    if not reqdata:
        return errorhttpresponse(400, 'No data in the request body')

    # parse data (oh wait... not needed :P)
    try:
        entity = request.json
    except ValueError as e:
        logging.error('Could not get JSON object from request body : {0} !'.format(str(e)))
        return errorhttpresponse(400, str(e))

    # create processus
    command = entity["cmd"]
    logging.info('Preparing subprocess for command : [%s]' % command)
    try:
        proc = subprocess.Popen(command, stdout=subprocess.PIPE, stderr=subprocess.PIPE);
    except OSError as e:
        logging.error('Could not execute command process : OSError({0}): {1} !'.format(e.errno, e.strerror))
        return errorhttpresponse(500, str(e))

    # go on !
    stdoutdata, stderrdata = proc.communicate()
    ret = proc.returncode
    logging.debug('Command [%s] executed with return value [%s]. stdout[%s] stderr[%s]' % (command, ret, str(stdoutdata), str(stderrdata)))

    # use Bottle's capacity to auto json (data + content type)
    return {'cmd':entity['cmd'], 'stdout':stdoutdata, 'stderr':stderrdata, 'retval':ret}


# ------------------------------------------------------------------------------
# ERRORS
# ------------------------------------------------------------------------------

@error(401)
def error401(error):
    return doerror(error)

@error(404)
def error404(error):
    return doerror(error)

@error(405)
def error405(error):
    return doerror(error)

@error(415)
def error415(error):
    return doerror(error)

@error(500)
def error500(error):
    return doerror(error)

@error(504)
def error504(error):
    return doerror(error)

def errorhttpresponse(status, msg):
    return HTTPResponse(json.dumps({'error': {'status': status, 'statusstr': HTTP_CODES[status], 'msg': msg}}, sort_keys=True, indent=4, separators=(',', ': ')), status, headers={"Content-Type":"application/json"})

def doerror(error):
    return errorhttpresponse(int(error.status[:3]), error.body)

# ------------------------------------------------------------------------------
# MAIN
# ------------------------------------------------------------------------------
def main():
    config = ConfigParser.RawConfigParser()
    config.read('cmdsrv.cfg')

    global VERSION
    global CHANGESET
    global CHANGESETDATE

    VERSION = config.get('cmdsrv', 'version')
    CHANGESET = config.get('cmdsrv', 'changeSet')
    CHANGESETDATE = config.get('cmdsrv', 'changeSetDate')

    logging.basicConfig(
        level=getattr(logging, config.get('logging', 'level').upper(), None),
        format=config.get('logging', 'format')
    )

    bindaddress = config.get('bottle', 'bindaddress')
    bindport = config.getint('bottle', 'bindport')

    print "=\n= Starting bottle cmdsrv on %s:%s\n=" % (bindaddress, bindport)

    run(host=bindaddress, port=bindport, reloader=config.getboolean('bottle', 'reloader'), debug=config.getboolean('bottle', 'debug'))

if __name__ == '__main__':
    sys.exit(main())
