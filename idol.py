import requests, re
import gevent

url="http://api.idolondemand.com/1/api/sync/{}/v1"
apikey="8e1741c1-1c77-4be0-b0eb-5084924de171"

def postrequests(function,data={},files={}):
    data["apikey"]=apikey
    callurl=url.format(function)
    r=requests.post(callurl,data=data,files=files)
    return r.json()


#logo recognition
def getlogo(filename):
    results = postrequests('recognizeimages', files= {'file': open(filename, 'rb')})
    print results
    return []

#barcode recognition
def getbarcode(filename):
    results = postrequests('recognizebarcodes', files= {'file': open(filename, 'rb')}, data={'barcode_type': 'qr'})
    return []


def try_gettext():
    results = postrequests('ocrdocument', files= {'file': open('tmp', 'rb')}, data={'mode': 'document_photo'})

    ret = []

    for block in results['text_block']:
        text = block['text']
        search = re.search('[0-9]\ [0-9]{3}\ [0-9]{3}\ [0-9]{4}', text)

        if search:
            ret.append(['number', search.group()])

    return ret

def try_gettextscene():
    results = postrequests('ocrdocument', files= {'file': open('tmp', 'rb')}, data={'mode': 'scene_photo'})

    ret = []

    for block in results['text_block']:
        text = block['text']
        search = re.search('[0-9]\ [0-9]{3}\ [0-9]{3}\ [0-9]{4}', text)

        if search:
            ret.append(['number', search.group()])

    return ret

getlogo('pics/logos')