import gevent.monkey

gevent.monkey.patch_all()

import itertools
import bottle
from bottle import response, request, hook, route

from idol import try_gettext, try_gettextscene, \
  try_getbarcode, try_getlogo, try_mashape

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
  print 'analysis started'
  img = request.files.get('image')
  img.save('tmp.jpg', overwrite=True)

  lst = [gevent.spawn(try_gettext), gevent.spawn(try_gettextscene),
    gevent.spawn(try_getbarcode), gevent.spawn(try_getlogo), gevent.spawn(try_mashape)]
  gevent.joinall(lst)

  res = list(itertools.chain(*[g.value for g in lst]))
  print(res)

  return 'text ' + ' '.join(res)

@bottle.route('/tmp.jpg')
def tmp():
  return bottle.static_file('tmp.jpg', '/root/overlay-backend/')

try:
  bottle.run(host='0.0.0.0', port=80, debug=False)
except:
  bottle.run(host='localhost', port=8080, debug=True)
