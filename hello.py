import bottle
from bottle import response, request, hook, route

import base64

@bottle.hook('after_request')
def enable_cors():
    response.headers['Access-Control-Allow-Origin'] = '*'
    response.headers['Access-Control-Allow-Methods'] = 'PUT, GET, POST, DELETE, OPTIONS'
    response.headers['Access-Control-Allow-Headers'] = 'Origin, Accept, Content-Type, X-Requested-With, X-CSRF-Token'

@bottle.get('/')
def health():
  response.content_type = 'application/json'
  return {'status': 'ok'}

@bottle.route('/analyse', method=['OPTIONS'])
def analyse_options():
  return None

@bottle.post('/analyse')
def analyse():
  try:
    #image_base64 = request.POST.get('image')

    img = request.files.get('image')
    img.save('tmp')
    return {'status': 'ok'}

  except:
    return {'status': 'oops'}

try:
  bottle.run(host='0.0.0.0', port=80, debug=False)
except:
  bottle.run(host='localhost', port=8080, debug=True)
