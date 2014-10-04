import requests
url="https://api.idolondemand.com/1/api/sync/ocrdocument/v1"
apikey="8e1741c1-1c77-4be0-b0eb-5084924de171"

def postrequests(function,data={},files={}):
   data["8e1741c1-1c77-4be0-b0eb-5084924de171"]=apikey
   callurl=url.format(function)
   r=requests.post(callurl,data=data,files=files)
   return r.json()

results=postrequests('querytextindex',{'text':'great'})
for document in results["documents"]:
   print document["title"]
