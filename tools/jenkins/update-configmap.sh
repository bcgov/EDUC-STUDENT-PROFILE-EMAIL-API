envValue=$1
APP_NAME=$2
OPENSHIFT_NAMESPACE=$3

TZVALUE="America/Vancouver"
SOAM_KC_REALM_ID="master"
KCADM_FILE_BIN_FOLDER="/tmp/keycloak-9.0.3/bin"
SOAM_KC=$OPENSHIFT_NAMESPACE-$envValue.pathfinder.gov.bc.ca
NATS_CLUSTER=educ_pen_nats_cluster
NATS_URL="nats://nats.${OPENSHIFT_NAMESPACE}-${envValue}.svc.cluster.local:4222"

oc project $OPENSHIFT_NAMESPACE-$envValue
CHES_CLIENT_ID=$(oc -o json get configmaps ${APP_NAME}-${envValue}-setup-config | sed -n "s/.*\"CHES_CLIENT_ID\": \"\(.*\)\",/\1/p")
CHES_CLIENT_SECRET=$(oc -o json get configmaps ${APP_NAME}-${envValue}-setup-config | sed -n "s/.*\"CHES_CLIENT_SECRET\": \"\(.*\)\",/\1/p")
CHES_TOKEN_URL=$(oc -o json get configmaps ${APP_NAME}-${envValue}-setup-config | sed -n "s/.*\"CHES_TOKEN_URL\": \"\(.*\)\"/\1/p")
CHES_ENDPOINT_URL=$(oc -o json get configmaps ${APP_NAME}-${envValue}-setup-config | sed -n "s/.*\"CHES_ENDPOINT_URL\": \"\(.*\)\",/\1/p")
SOAM_KC_LOAD_USER_ADMIN=$(oc -o json get secret sso-admin-${envValue} | sed -n 's/.*"username": "\(.*\)"/\1/p' | base64 --decode)
SOAM_KC_LOAD_USER_PASS=$(oc -o json get secret sso-admin-${envValue} | sed -n 's/.*"password": "\(.*\)",/\1/p' | base64 --decode)
URL_LOGIN_BASIC="https://pen-request-${OPENSHIFT_NAMESPACE}-${envValue}.pathfinder.gov.bc.ca/api/auth/login_bceid"
URL_LOGIN_BCSC="https://pen-request-${OPENSHIFT_NAMESPACE}-${envValue}.pathfinder.gov.bc.ca/api/auth/login_bcsc"

oc project $OPENSHIFT_NAMESPACE-tools

if [ "$envValue" != "prod" ]
then
    URL_LOGIN_BASIC=https://pen-request-$OPENSHIFT_NAMESPACE-$envValue.pathfinder.gov.bc.ca/api/auth/login_bceid
    URL_LOGIN_BCSC=https://pen-request-$OPENSHIFT_NAMESPACE-$envValue.pathfinder.gov.bc.ca/api/auth/login_bcsc
else
    URL_LOGIN_BASIC=https://getmypen.gov.bc.ca/api/auth/login_bceid
    URL_LOGIN_BCSC=https://getmypen.gov.bc.ca/api/auth/login_bcsc
fi

###########################################################
#Fetch the public key
###########################################################
$KCADM_FILE_BIN_FOLDER/kcadm.sh config credentials --server https://$SOAM_KC/auth --realm $SOAM_KC_REALM_ID --user $SOAM_KC_LOAD_USER_ADMIN --password $SOAM_KC_LOAD_USER_PASS
getPublicKey(){
    executorID= $KCADM_FILE_BIN_FOLDER/kcadm.sh get keys -r $SOAM_KC_REALM_ID | grep -Po 'publicKey" : "\K([^"]*)'
}

echo Fetching public key from SOAM
soamFullPublicKey="-----BEGIN PUBLIC KEY----- $(getPublicKey) -----END PUBLIC KEY-----"
newline=$'\n'
formattedPublicKey="${soamFullPublicKey:0:26}${newline}${soamFullPublicKey:27:64}${newline}${soamFullPublicKey:91:64}${newline}${soamFullPublicKey:155:64}${newline}${soamFullPublicKey:219:64}${newline}${soamFullPublicKey:283:64}${newline}${soamFullPublicKey:347:64}${newline}${soamFullPublicKey:411:9}${newline}${soamFullPublicKey:420}"

#SEND_PEN_REQUEST_EMAIL
$KCADM_FILE_BIN_FOLDER/kcadm.sh create client-scopes -r $SOAM_KC_REALM_ID --body "{\"description\": \"PEN Request send email scope\",\"id\": \"SEND_PEN_REQUEST_EMAIL\",\"name\": \"SEND_PEN_REQUEST_EMAIL\",\"protocol\": \"openid-connect\",\"attributes\" : {\"include.in.token.scope\" : \"true\",\"display.on.consent.screen\" : \"false\"}}"

###########################################################
#Setup for config-map
###########################################################
echo Creating config map $APP_NAME-config-map 
oc create -n $OPENSHIFT_NAMESPACE-$envValue configmap $APP_NAME-config-map --from-literal=URL_LOGIN_BASIC="$URL_LOGIN_BASIC" --from-literal=URL_LOGIN_BCSC="$URL_LOGIN_BCSC" --from-literal=TZ=$TZVALUE --from-literal=SOAM_PUBLIC_KEY="$soamFullPublicKey" --from-literal=CHES_CLIENT_ID=$CHES_CLIENT_ID --from-literal=CHES_CLIENT_SECRET=$CHES_CLIENT_SECRET --from-literal=CHES_TOKEN_URL="$CHES_TOKEN_URL" --from-literal=CHES_ENDPOINT_URL="$CHES_ENDPOINT_URL" --from-literal=SPRING_SECURITY_LOG_LEVEL=INFO --from-literal=SPRING_WEB_LOG_LEVEL=INFO --from-literal=APP_LOG_LEVEL=INFO --from-literal=SPRING_BOOT_AUTOCONFIG_LOG_LEVEL=INFO --from-literal=SPRING_SHOW_REQUEST_DETAILS=false --from-literal=EMAIL_TEMPLATE_COMPLETED_REQUEST="<!DOCTYPE html><html><head><meta charset=\"ISO-8859-1\"><title>Your Personal Education Number(PEN) Request</title></head><body>Hello {0},<br><br><b>We have located your PEN</b><br><br>Steps to access your PEN:<ol><li>Click this link <a href={1}>here</a></li><li>Log in using your BCeID (the same method you did when submitting the original request)</li></ol>Note: if your demographic information (name, gender or date of birth) has changed since you last attended a B.C. school, and if you are creating a Student Transcript Services (STS) account to order your transcript from the Ministry of Education, <strong>then please wait until tomorrow morning</strong> for the overnight update to finalize. When you register on STS, be sure you are using your current legal name format and <strong>NOT a maiden name</strong>.  Your transcript will be generated using your current legal name format listed below.<br><br>If the above link doesn't work, please paste this link into your web browser's address field:<br><br><a href={2}>{3}</a><br><br>Regards,<br>PEN Team, B.C. Ministry of Education</body></html>" --from-literal=EMAIL_TEMPLATE_COMPLETED_REQUEST_DEMOGRAPHIC_CHANGE="<!DOCTYPE html><html><head><meta charset=\"ISO-8859-1\"><title>Your Personal Education Number(PEN) Request</title></head><body>Hello {0},<br><br><b>We have located your PEN</b><br><br>Steps to access your PEN:<ol><li>Click this link <a href={1}>here</a></li><li>Log in using your BCeID (the same method you did when submitting the original request)</li></ol>Note: Your demographic information (name, gender or date of birth) reported to PEN has been updated since you last attended a B.C. school.  If you are planning on creating a Student Transcript Services (STS) account to order your transcript from the Ministry of Education, <strong>then please wait until tomorrow morning</strong> for the overnight update to finalize before you proceed with STS. When you register on STS, be sure you are using your current legal name format reported to PEN and <strong>NOT a maiden name</strong>.  Your transcript will be generated using your current legal name format noted on your PEN account.<br><br>If the above link doesn't work, please paste this link into your web browser's address field:<br><br><a href={2}>{3}</a><br><br>Regards,<br>PEN Team, B.C. Ministry of Education</body></html>" --from-literal=EMAIL_TEMPLATE_REJECTED_REQUEST="<!DOCTYPE html><html><head><meta charset=\"ISO-8859-1\"><title>Your Personal Education Number(PEN) Request</title></head><body>Hello,<br><br><b>Your Personal Education Number (PEN) request could not be fulfilled</b> for the following reason(s):<br><br><b><i>{0}</i></b><br><br>Please review the above reason(s) and the information you provided.<br>If any of the information above is incorrect, you can make another PEN request or contact the <a href="mailto:pens.coordinator@gov.bc.ca">pens.coordinator@gov.bc.ca</a>.<br>To login to GetMyPEN click <a href={1}>here</a> and log in using your BCeID.<br><br>If the above link doesn't work, please paste this link into your web browser's address field:<br><br><a href={2}>{3}</a><br><br>Regards,<br>PEN Team, B.C. Ministry of Education</body></html>" --from-literal=EMAIL_TEMPLATE_ADDITIONAL_INFO="<!DOCTYPE html><html><head><meta charset=\"ISO-8859-1\"><title>Your Personal Education Number(PEN) Request</title></head><body>Hello,<br><br><b>Your Personal Education Number (PEN) request is in progress but, we do not have enough information to locate your PEN.</b><br><br>Steps to provide additional information:<ol><li>Click this link <a href={0}>here</a></li><li>Log in using the same method you did when submitting the original request and</li><li>Respond to the additional information request</li></ol>If the above link doesn't work, please paste this link into your web browser's address field:<br><br><a href={1}>{2}</a><br><br>Regards,<br>PEN Team, B.C. Ministry of Education</body></html>" --from-literal=EMAIL_TEMPLATE_VERIFY_EMAIL="<!DOCTYPE html><html><head><meta charset=\"ISO-8859-1\"><title>Activate your GetMyPEN request within 24 hours of receiving this email</title></head><body>Hello,<br><br>You have requested your Personal Education Number from the Ministry of Education.<br><br>To get started we need to verify your identity and link your {0} account to your GetMyPEN request.<br><br>You have <b>24 hours</b> after receiving this email to: <ol><li><a href={1}={2}>Activate your GetMyPEN</a> request</li><li>Then, login using the same {3} account</li></ol>If the activation link above doesn't work, please paste this link into your web browser's address field:<br><br><a href={4}={5}>{6}={7}</a><br><br>If you are not able to activate your account, you will have to log into GetMyPEN.gov.bc.ca and resend the <b>Verification Email</b>.<br><br>If you have received this message in error, please contact <a href="mailto:pens.coordinator@gov.bc.ca">pens.coordinator@gov.bc.ca</a><br><br>Regards,<br>PEN Coordinator, B.C. Ministry of Education</body></html>" --dry-run -o yaml | oc apply -f -
echo
echo Setting environment variables for $APP_NAME-$SOAM_KC_REALM_ID application
oc project $OPENSHIFT_NAMESPACE-$envValue
oc set env --from=configmap/$APP_NAME-config-map dc/$APP_NAME-$SOAM_KC_REALM_ID