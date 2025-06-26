Run with command below:

Linux:
```
helm install smarthome-app . \
  --set nextapp.clientid="{GITHUB_CLIENT_ID}" \
  --set nextapp.clientsecret="{GITHUB_CLIENT_SECRET}" \
  --set nextapp.nextauthsecret="{NEXT_AUTH_SECRET}"
```

Windows:
```
helm install smarthome-app . --set nextapp.clientid="{GITHUB_CLIENT_ID}" --set nextapp.clientsecret="{GITHUB_CLIENT_SECRET}" --set nextapp.nextauthsecret="{NEXT_AUTH_SECRET}"
```