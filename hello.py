import bottle
from bottle import response, request, hook

import base64

@bottle.hook('after_request')
def enable_cors():
    response.headers['Access-Control-Allow-Origin'] = '*'

@bottle.get('/')
def health():
  response.content_type = 'application/json'
  return {'status': 'ok'}

@bottle.post('/analyse')
def analyse():
  image_base64 = request.POST.get('image')

try:
  bottle.run(host='0.0.0.0', port=80, debug=False)
except:
  bottle.run(host='localhost', port=8080, debug=True)
