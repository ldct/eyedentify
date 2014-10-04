from bottle import route, run, response

@route('/')
def hello():
  response.content_type = 'application/json'
  return {'status': 'ok'}
try:
  run(host='0.0.0.0', port=80, debug=False)
except:
  run(host='localhost', port=8080, debug=True)
