import requests, re
import gevent

url="http://api.idolondemand.com/1/api/sync/{}/v1"
apikey="8e1741c1-1c77-4be0-b0eb-5084924de171"

def postrequests(function,data={},files={}):
    data["apikey"]=apikey
    callurl=url.format(function)
    r=requests.post(callurl,data=data,files=files)
    return r.json()

f = open('pics/microsoft.jpg', 'rb')

def gettext(filename):
    #todo: sharpen
    results = postrequests('ocrdocument', files= {'file': open(filename, 'rb')}, data={'mode': 'document_photo'})

    ret = []

    for block in results['text_block']:
        text = block['text']
        search = re.search('[0-9]\ [0-9]{3}\ [0-9]{3}\ [0-9]{4}', text)

        if search:
            ret.append(search.group())

    return ret

#text from image
def gettextscene(filename):
    results = postrequests('ocrdocument', files= {'file': open(filename, 'rb')}, data={'mode': 'scene_photo'})

    ret = []

    for block in results['text_block']:
        text = block['text']
        search = re.search('[0-9]\ [0-9]{3}\ [0-9]{3}\ [0-9]{4}', text)

        if search:
            ret.append(search.group())

    return ret

#logo recognition
def getlogo(filename):
    results = postrequests('recognizeimages', files= {'file': open(filename, 'rb')}, indexes={'indexes': 'corporatelogos'})
    return []

#barcode recognition
def getbarcode(filename):
    results = postrequests('recognizebarcodes', files= {'file': open(filename, 'rb')}, data={'barcode_type': 'qr'})
    return []

def try_gettext():
    res = gettext('tmp')
    print res
def try_gettextscene():
    res = gettextscene('tmp')
    print res