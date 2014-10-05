import requests, re
import gevent

url="http://api.idolondemand.com/1/api/sync/{}/v1"
apikey="8e1741c1-1c77-4be0-b0eb-5084924de171"

def postrequests(function,data={},files={}):
    data["apikey"]=apikey
    callurl=url.format(function)
    r=requests.post(callurl,data=data,files=files)
    return r.json()


def try_getlogo():
    results = postrequests('recognizeimages', files= {'file': open('tmp.jpg', 'rb')}, data={'image_type': 'complex_3d'})
    return [o['unique_name'] for o in results['object']]

def try_getbarcode():

    from subprocess import call
    call(['convert', '-brightness-contrast', 'x100', '-resize', '30%', 'tmp.jpg', 'tmp_light.jpg'])

    results = postrequests('recognizebarcodes', files= {'file': open('tmp_light.jpg', 'rb')}, data={'barcode_type': 'qr'})
    barcode = results['barcode']

    if len(barcode):
        return [bc['text'] for bc in barcode]
    else:
        return []

def try_gettext():
    results = postrequests('ocrdocument', files= {'file': open('tmp.jpg', 'rb')}, data={'mode': 'document_photo'})

    ret = []

    for block in results['text_block']:
        text = block['text']
        search = re.search('[0-9]\ [0-9]{3}\ [0-9]{3}\ [0-9]{4}', text)

        if search:
            ret.append(['number', search.group()])

    return ret

def try_gettextscene():
    results = postrequests('ocrdocument', files= {'file': open('tmp.jpg', 'rb')}, data={'mode': 'scene_photo'})

    ret = []

    for block in results['text_block']:
        text = block['text']
        search = re.search('[0-9]\ [0-9]{3}\ [0-9]{3}\ [0-9]{4}', text)

        if search:
            ret.append(['number', search.group()])

    return ret