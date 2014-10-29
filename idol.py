import requests, re
import gevent
import unirest
import secrets

url = "http://api.idolondemand.com/1/api/sync/{}/v1"
apikey = secrets.IDOL_API_KEY

def postrequests(function,data={},files={}):
    data["apikey"]=apikey
    callurl=url.format(function)
    r=requests.post(callurl,data=data,files=files)
    return r.json()


def try_getlogo():
    results = postrequests('recognizeimages', files= {'file': open('tmp.jpg', 'rb')}, data={'image_type': 'complex_3d'})
    obj = results['object']
    if len(obj):
        symbol = obj[0]['unique_name']
        info = requests.get('http://dev.markitondemand.com/Api/v2/Quote/json?symbol=' + symbol).json()
        return ['com ' + info['Name'] + ' $' + str(info['LastPrice'])]
    else:
        return []

def try_getbarcode():

    from subprocess import call
    call(['convert', '-brightness-contrast', 'x100', '-resize', '30%', 'tmp.jpg', 'tmp_light.jpg'])

    results = postrequests('recognizebarcodes', files= {'file': open('tmp_light.jpg', 'rb')}, data={'barcode_type': 'qr'})
    barcode = results['barcode']

    if len(barcode):
        return ['url ' + bc['text'] for bc in barcode]
    else:
        return []

def try_gettext():
    results = postrequests('ocrdocument', files= {'file': open('tmp.jpg', 'rb')}, data={'mode': 'document_photo'})

    for block in results['text_block']:
        text = block['text']
        search = re.search('[0-9]\ [0-9]{3}\ [0-9]{3}\ [0-9]{4}', text)

        if search:
            return ['txt ' + search.group()]

    return []

def try_gettextscene():
    results = postrequests('ocrdocument', files= {'file': open('tmp.jpg', 'rb')}, data={'mode': 'scene_photo'})

    for block in results['text_block']:
        text = block['text']
        search = re.search('[0-9]\ [0-9]{3}\ [0-9]{3}\ [0-9]{4}', text)

        if search:
            return ['txt ' + search.group()]

    return []

def try_mashape():
    response = unirest.post("https://camfind.p.mashape.com/image_requests",
      headers={"X-Mashape-Key": secrets.MASHAPE_SECRET},
      params={"image_request[image]": open('tmp.jpg', mode="r"), "image_request[locale]": "en_US"}
    )
    token = response.body['token']
    print token

    while True:
        response = unirest.get("https://camfind.p.mashape.com/image_responses/" + token,
          headers={"X-Mashape-Key": "secrets.MASHAPE_SECRET"}
        )
        gevent.sleep(0.5)
        print response.body
        if (response.body['status'] == 'completed'):
            return ['txt ' + response.body['name']]

    return []