Run with command below:

helm install at-app . \
  --set nextapp.clientid="{GITHUB_CLIENT_ID}" \
  --set nextapp.clientsecret="{GITHUB_CLIENT_SECRET}" \
  --set nextapp.nextauthsecret="{NEXT_AUTH_SECRET}"
