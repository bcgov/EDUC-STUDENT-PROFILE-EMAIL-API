envValue=$1
APP_NAME=$2
PEN_NAMESPACE=$3
COMMON_NAMESPACE=$4
DB_JDBC_CONNECT_STRING=$5
DB_PWD=$6
DB_USER=$7
SPLUNK_TOKEN=$8
CHES_CLIENT_ID=$9
CHES_CLIENT_SECRET=${10}
CHES_TOKEN_URL=${11}
CHES_ENDPOINT_URL=${12}

TZVALUE="America/Vancouver"
SOAM_KC_REALM_ID="master"

SOAM_KC="soam-$envValue.apps.silver.devops.gov.bc.ca"
NATS_CLUSTER=educ_nats_cluster
NATS_URL="nats://nats.${COMMON_NAMESPACE}-${envValue}.svc.cluster.local:4222"

SOAM_KC_LOAD_USER_ADMIN=$(oc -n "$COMMON_NAMESPACE-$envValue" -o json get secret "sso-admin-${envValue}" | sed -n 's/.*"username": "\(.*\)"/\1/p' | base64 --decode)
SOAM_KC_LOAD_USER_PASS=$(oc -n "$COMMON_NAMESPACE-$envValue" -o json get secret "sso-admin-${envValue}" | sed -n 's/.*"password": "\(.*\)",/\1/p' | base64 --decode)

URL_LOGIN_BASIC_GMP="https://student-profile-${PEN_NAMESPACE}-$envValue.getmypen.gov.bc.ca/api/auth/login_bceid_gmp"
URL_LOGIN_BASIC_UMP="https://student-profile-${PEN_NAMESPACE}-$envValue.getmypen.gov.bc.ca/api/auth/login_bceid_ump"
URL_LOGIN_BCSC_GMP="https://student-profile-${PEN_NAMESPACE}-$envValue.getmypen.gov.bc.ca/api/auth/login_bcsc_gmp"
URL_LOGIN_BCSC_UMP="https://student-profile-${PEN_NAMESPACE}-$envValue.getmypen.gov.bc.ca/api/auth/login_bcsc_ump"

if [ "$envValue" != "prod" ]; then
  URL_LOGIN_BASIC_GMP=https://$envValue.getmypen.gov.bc.ca/api/auth/login_bceid_gmp
  URL_LOGIN_BASIC_UMP=https://$envValue.getmypen.gov.bc.ca/api/auth/login_bceid_ump
  URL_LOGIN_BCSC_GMP=https://$envValue.getmypen.gov.bc.ca/api/auth/login_bcsc_gmp
  URL_LOGIN_BCSC_UMP=https://$envValue.getmypen.gov.bc.ca/api/auth/login_bcsc_ump
else
  URL_LOGIN_BASIC_GMP=https://getmypen.gov.bc.ca/api/auth/login_bceid_gmp
  URL_LOGIN_BASIC_UMP=https://getmypen.gov.bc.ca/api/auth/login_bceid_ump
  URL_LOGIN_BCSC_GMP=https://getmypen.gov.bc.ca/api/auth/login_bcsc_gmp
  URL_LOGIN_BCSC_UMP=https://getmypen.gov.bc.ca/api/auth/login_bcsc_ump
fi

echo Fetching SOAM token
TKN=$(curl -s \
  -d "client_id=admin-cli" \
  -d "username=$SOAM_KC_LOAD_USER_ADMIN" \
  -d "password=$SOAM_KC_LOAD_USER_PASS" \
  -d "grant_type=password" \
  "https://$SOAM_KC/auth/realms/$SOAM_KC_REALM_ID/protocol/openid-connect/token" | jq -r '.access_token')

echo
echo Writing scope SEND_STUDENT_PROFILE_EMAIL
curl -sX POST "https://$SOAM_KC/auth/admin/realms/$SOAM_KC_REALM_ID/client-scopes" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TKN" \
  -d "{\"description\": \"Student Profile send email scope\",\"id\": \"SEND_STUDENT_PROFILE_EMAIL\",\"name\": \"SEND_STUDENT_PROFILE_EMAIL\",\"protocol\": \"openid-connect\",\"attributes\" : {\"include.in.token.scope\" : \"true\",\"display.on.consent.screen\" : \"false\"}}"

###########################################################
#Setup for config-map
###########################################################
SPLUNK_URL="gww.splunk.educ.gov.bc.ca"
FLB_CONFIG="[SERVICE]
   Flush        1
   Daemon       Off
   Log_Level    debug
   HTTP_Server   On
   HTTP_Listen   0.0.0.0
   Parsers_File parsers.conf
[INPUT]
   Name   tail
   Path   /mnt/log/*
   Exclude_Path *.gz,*.zip
   Parser docker
   Mem_Buf_Limit 20MB
[FILTER]
   Name record_modifier
   Match *
   Record hostname \${HOSTNAME}
[OUTPUT]
   Name   stdout
   Match  *
[OUTPUT]
   Name  splunk
   Match *
   Host  $SPLUNK_URL
   Port  443
   TLS         On
   TLS.Verify  Off
   Message_Key $APP_NAME
   Splunk_Token $SPLUNK_TOKEN
"
PARSER_CONFIG="
[PARSER]
    Name        docker
    Format      json
"

echo Creating config map "$APP_NAME-config-map"
oc create -n "$PEN_NAMESPACE-$envValue" configmap "$APP_NAME-config-map" \
  --from-literal=URL_LOGIN_BASIC_GMP="$URL_LOGIN_BASIC_GMP" \
  --from-literal=URL_LOGIN_BCSC_GMP="$URL_LOGIN_BCSC_GMP" \
  --from-literal=URL_LOGIN_BASIC_UMP="$URL_LOGIN_BASIC_UMP" \
  --from-literal=URL_LOGIN_BCSC_UMP="$URL_LOGIN_BCSC_UMP" \
  --from-literal=TZ="$TZVALUE" \
  --from-literal=CHES_CLIENT_ID="$CHES_CLIENT_ID" \
  --from-literal=CHES_CLIENT_SECRET="$CHES_CLIENT_SECRET" \
  --from-literal=CHES_TOKEN_URL="$CHES_TOKEN_URL" \
  --from-literal=CHES_ENDPOINT_URL="$CHES_ENDPOINT_URL" \
  --from-literal=SPRING_SECURITY_LOG_LEVEL=INFO \
  --from-literal=SPRING_WEB_LOG_LEVEL=INFO \
  --from-literal=APP_LOG_LEVEL=INFO \
  --from-literal=SPRING_BOOT_AUTOCONFIG_LOG_LEVEL=INFO \
  --from-literal=SPRING_SHOW_REQUEST_DETAILS=false \
  --from-literal=EMAIL_TEMPLATE_COMPLETED_REQUEST_UMP="<!DOCTYPE html><html xmlns:th=\"http://www.thymeleaf.org\"><head><meta charset=\"ISO-8859-1\"><title>Your Personal Education Number(PEN) Info Update Request</title></head><body>Hello <span th:text=\"\${\$}{firstName}\"></span>,<br><br><b>We have updated your PEN information</b><br><br>Steps to access your changes:<ol><li>Click this link <a th:href=\"@{\${\$}{loginUrl}}\">here</a></li><li>Log in using the same method you did when submitting the original request</li></ol>Your demographic information (name or date of birth) reported to PEN information has been updated so if you are planning on creating a Student Transcript Services (STS) account to order your transcript from the Ministry of Education and Child Care, <strong>then please wait until tomorrow morning</strong> for the overnight update to finalize before you proceed with STS. When you register on STS, be sure you are using your current legal name format and <strong>NOT a maiden name</strong>. Your transcript will be generated using your current legal name.<br><br>If the above link doesn't work, please paste this link into your web browser's address field:<br><br><a th:href=\"@{\${\$}{loginUrl}}\" th:text=\"\${\$}{loginUrl}\"></a><br><br>Regards,<br>PEN Team, B.C. Ministry of Education and Child Care</body></html>" \
  --from-literal=EMAIL_TEMPLATE_REJECTED_REQUEST_UMP="<!DOCTYPE html><html xmlns:th=\"http://www.thymeleaf.org\"><head><meta charset=\"ISO-8859-1\"><title>Your Personal Education Number(PEN) Info Update Request</title></head><body>Hello,<br><br><b>Your PEN info update request could not be fulfilled</b> for the following reason(s):<br><br><b><i th:text=\"\${\$}{rejectionReason}\"></i></b><br><br>Please review the above reason(s) and the information you provided.<br>If any of the information above is incorrect, you can make another PEN info update request or contact the <a href=\"mailto:pens.coordinator@gov.bc.ca\">pens.coordinator@gov.bc.ca</a>.<br>To login to UpdateMyPENInfo click <a th:href=\"@{\${\$}{loginUrl}}\">here</a> and log in.<br><br>If the above link doesn't work, please paste this link into your web browser's address field:<br><br><a th:href=\"@{\${\$}{loginUrl}}\" th:text=\"\${\$}{loginUrl}\"></a><br><br>Regards,<br>PEN Team, B.C. Ministry of Education and Child Care</body></html>" \
  --from-literal=EMAIL_TEMPLATE_ADDITIONAL_INFO_UMP="<!DOCTYPE html><html xmlns:th=\"http://www.thymeleaf.org\"><head><meta charset=\"ISO-8859-1\"><title>Your Personal Education Number(PEN) Info Update Request</title></head><body>Hello,<br><br><b>Your request to have your PEN info updated is in progress, but we do not have enough information to update your record.</b><br><br>Steps to provide additional information:<ol><li>Click this link <a th:href=\"@{\${\$}{loginUrl}}\">here</a></li><li>Log in using the same method you did when submitting the original request and</li><li>Respond to the additional information request</li></ol>If the above link doesn't work, please paste this link into your web browser's address field:<br><br><a th:href=\"@{\${\$}{loginUrl}}\" th:text=\"\${\$}{loginUrl}\"></a><br><br>Regards,<br>PEN Team, B.C. Ministry of Education and Child Care</body></html>" \
  --from-literal=EMAIL_TEMPLATE_VERIFY_EMAIL_UMP="<!DOCTYPE html><html xmlns:th=\"http://www.thymeleaf.org\"><head><meta charset=\"ISO-8859-1\"><title>Activate your UpdateMyPENInfo request within 24 hours of receiving this email</title></head><body>Hello,<br><br>You have requested for PEN information updates from the Ministry of Education and Child Care.<br><br>To get started we need to verify your identity and link your <span th:text=\"\${\$}{identityTypeLabel}\"></span> account to your UpdateMyPENInfo request.<br><br>You have <b>24 hours</b> after receiving this email to:<ol><li><a th:href=\"@{\${\$}{verificationUrl}+'='+\${\$}{jwtToken}}\">Activate your UpdateMyPENInfo</a> request</li><li>Then, login using the same <span th:text=\"\${\$}{identityTypeLabel}\"></span> account</li></ol>If the activation link above doesn't work, please paste this link into your web browser's address field:<br><br><a th:href=\"@{\${\$}{verificationUrl}+'='+\${\$}{jwtToken}}\" th:text=\"@{\${\$}{verificationUrl}+'='+\${\$}{jwtToken}}\"></a><br><br>If you are not able to activate your account, you will have to log into UpdateMyPENInfo.gov.bc.ca and resend the <b>Verification Email</b>.<br><br>If you have received this message in error, please contact <a href=\"mailto:pens.coordinator@gov.bc.ca\">pens.coordinator@gov.bc.ca</a><br><br>Regards,<br>PEN Coordinator, B.C. Ministry of Education and Child Care</body></html>" \
  --from-literal=EMAIL_TEMPLATE_COMPLETED_REQUEST_GMP="<!DOCTYPE html><html xmlns:th=\"http://www.thymeleaf.org\"><head><meta charset=\"ISO-8859-1\"><title>Your Personal Education Number(PEN) Request</title></head><body>Hello <span th:text=\"\${\$}{firstName}\"></span>,<br><br><b>We have located your PEN</b><br><br>Steps to access your PEN:<ol><li>Click this link <a th:href=\"@{\${\$}{loginUrl}}\">here</a></li><li>Log in using the same method you did when submitting the original request</li></ol>Note: if your demographic information (name or date of birth) has changed since you last attended a B.C. school, and if you are creating a Student Transcript Services (STS) account to order your transcript from the Ministry of Education and Child Care, <strong>then please wait until tomorrow morning</strong> for the overnight update to finalize. When you register on STS, be sure you are using your current legal name format and <strong>NOT a maiden name</strong>. Your transcript will be generated using your current legal name format listed below.<br><br>If the above link doesn't work, please paste this link into your web browser's address field:<br><br><a th:href=\"@{\${\$}{loginUrl}}\" th:text=\"\${\$}{loginUrl}\"></a><br><br>Regards,<br>PEN Team, B.C. Ministry of Education and Child Care</body></html>" \
  --from-literal=EMAIL_TEMPLATE_COMPLETED_REQUEST_DEMOGRAPHIC_CHANGE_GMP="<!DOCTYPE html><html xmlns:th=\"http://www.thymeleaf.org\"><head><meta charset=\"ISO-8859-1\"><title>Your Personal Education Number(PEN) Request</title></head><body>Hello <span th:text=\"\${\$}{firstName}\"></span>,<br><br><b>We have located your PEN</b><br><br>Steps to access your PEN:<ol><li>Click this link <a th:href=\"@{\${\$}{loginUrl}}\">here</a></li><li>Log in using the same method you did when submitting the original request</li></ol>Note: Your demographic information (name or date of birth) reported to PEN has been updated since you last attended a B.C. school. If you are planning on creating a Student Transcript Services (STS) account to order your transcript from the Ministry of Education and Child Care, <strong>then please wait until tomorrow morning</strong> for the overnight update to finalize before you proceed with STS. When you register on STS, be sure you are using your current legal name format reported to PEN and <strong>NOT a maiden name</strong>. Your transcript will be generated using your current legal name format noted on your PEN account.<br><br>If the above link doesn't work, please paste this link into your web browser's address field:<br><br><a th:href=\"@{\${\$}{loginUrl}}\" th:text=\"\${\$}{loginUrl}\"></a><br><br>Regards,<br>PEN Team, B.C. Ministry of Education and Child Care</body></html>" \
  --from-literal=EMAIL_TEMPLATE_REJECTED_REQUEST_GMP="<!DOCTYPE html><html xmlns:th=\"http://www.thymeleaf.org\"><head><meta charset=\"ISO-8859-1\"><title>Your Personal Education Number(PEN) Request</title></head><body>Hello,<br><br><b>Your Personal Education Number (PEN) request could not be fulfilled</b> for the following reason(s):<br><br><b><i th:text=\"\${\$}{rejectionReason}\"></i></b><br><br>Please review the above reason(s) and the information you provided.<br>If any of the information above is incorrect, you can make another PEN request or contact the <a href=\"mailto:pens.coordinator@gov.bc.ca\">pens.coordinator@gov.bc.ca</a>.<br>To login to GetMyPEN click <a th:href=\"@{\${\$}{loginUrl}}\">here</a> and log in.<br><br>If the above link doesn't work, please paste this link into your web browser's address field:<br><br><a th:href=\"@{\${\$}{loginUrl}}\" th:text=\"\${\$}{loginUrl}\"></a><br><br>Regards,<br>PEN Team, B.C. Ministry of Education and Child Care</body></html>" \
  --from-literal=EMAIL_TEMPLATE_ADDITIONAL_INFO_GMP="<!DOCTYPE html><html xmlns:th=\"http://www.thymeleaf.org\"><head><meta charset=\"ISO-8859-1\"><title>Your Personal Education Number(PEN) Request</title></head><body>Hello,<br><br><b>Your Personal Education Number (PEN) request is in progress but, we do not have enough information to locate your PEN.</b><br><br>Steps to provide additional information:<ol><li>Click this link <a th:href=\"@{\${\$}{loginUrl}}\">here</a></li><li>Log in using the same method you did when submitting the original request and</li><li>Respond to the additional information request</li></ol>If the above link doesn't work, please paste this link into your web browser's address field:<br><br><a th:href=\"@{\${\$}{loginUrl}}\" th:text=\"\${\$}{loginUrl}\"></a><br><br>Regards,<br>PEN Team, B.C. Ministry of Education and Child Care</body></html>" \
  --from-literal=EMAIL_TEMPLATE_VERIFY_EMAIL_GMP="<!DOCTYPE html><html xmlns:th=\"http://www.thymeleaf.org\"><head><meta charset=\"ISO-8859-1\"><title>Activate your GetMyPEN request within 24 hours of receiving this email</title></head><body>Hello,<br><br>You have requested your Personal Education Number from the Ministry of Education and Child Care.<br><br>To get started we need to verify your identity and link your <span th:text=\"\${\$}{identityTypeLabel}\"></span> account to your GetMyPEN request.<br><br>You have <b>24 hours</b> after receiving this email to:<ol><li><a th:href=\"@{\${\$}{verificationUrl}+'='+\${\$}{jwtToken}}\">Activate your GetMyPEN</a> request</li><li>Then, login using the same <span th:text=\"\${\$}{identityTypeLabel}\"></span> account</li></ol>If the activation link above doesn't work, please paste this link into your web browser's address field:<br><br><a th:href=\"@{\${\$}{verificationUrl}+'='+\${\$}{jwtToken}}\" th:text=\"@{\${\$}{verificationUrl}+'='+\${\$}{jwtToken}}\"></a><br><br>If you are not able to activate your account, you will have to log into GetMyPEN.gov.bc.ca and resend the <b>Verification Email</b>.<br><br>If you have received this message in error, please contact <a href=\"mailto:pens.coordinator@gov.bc.ca\">pens.coordinator@gov.bc.ca</a><br><br>Regards,<br>PEN Coordinator, B.C. Ministry of Education and Child Care</body></html>" \
  --from-literal=NATS_URL="$NATS_URL" \
  --from-literal=NATS_CLUSTER="$NATS_CLUSTER" \
  --from-literal=DB_URL_PROFILE_REQ_EMAIL_API="$DB_JDBC_CONNECT_STRING" \
  --from-literal=DB_USERNAME_PROFILE_REQ_EMAIL_API="$DB_USER" \
  --from-literal=DB_PASSWORD_PROFILE_REQ_EMAIL_API="$DB_PWD" \
  --from-literal=EMAIL_TEMPLATE_NOTIFY_STALE_RETURN_GMP="<!DOCTYPE html><html xmlns:th=\"http://www.thymeleaf.org\"><head><meta charset=\"ISO-8859-1\"><title>Your Personal Education Number(PEN) Request</title></head><body>Hello,<br><br><b>This is a reminder that additional information is required in order to complete your GetMyPEN request. In order to proceed please log in and provide requested information within 48 hours or your request will be cancelled.</b><br><br>Steps to provide additional information:<ol><li>Click this link <a th:href=\"@{\${\$}{loginUrl}}\">here</a></li><li>Log in using the same method you did when submitting the original request and</li><li>Respond to the additional information request</li></ol>If the above link doesn't work, please paste this link into your web browser's address field:<br><br><a th:href=\"@{\${\$}{loginUrl}}\" th:text=\"\${\$}{loginUrl}\"></a><br><br>Regards,<br>PEN Team, B.C. Ministry of Education and Child Care</body></html>" \
  --from-literal=EMAIL_TEMPLATE_NOTIFY_STALE_RETURN_UMP="<!DOCTYPE html><html xmlns:th=\"http://www.thymeleaf.org\"><head><meta charset=\"ISO-8859-1\"><title>Your Personal Education Number(PEN) Info Update Request</title></head><body>Hello,<br><br><b>This is a reminder that additional information is required in order to complete your UpdateMyPENInfo request. In order to proceed please log in and provide requested information within 48 hours or your request will be cancelled.</b><br><br>Steps to provide additional information:<ol><li>Click this link <a th:href=\"@{\${\$}{loginUrl}}\">here</a></li><li>Log in using the same method you did when submitting the original request and</li><li>Respond to the additional information request</li></ol>If the above link doesn't work, please paste this link into your web browser's address field:<br><br><a th:href=\"@{\${\$}{loginUrl}}\" th:text=\"\${\$}{loginUrl}\"></a><br><br>Regards,<br>PEN Team, B.C. Ministry of Education and Child Care</body></html>" \
  --from-literal=SCHEDULED_JOBS_POLL_EVENTS="0/1 * * * * *" \
  --from-literal=SCHEDULED_JOBS_POLL_EVENTS_LOCK_AT_LEAST_FOR="800ms" \
  --from-literal=SCHEDULED_JOBS_POLL_EVENTS_LOCK_AT_MOST_FOR="900ms" \
  --from-literal=TOKEN_ISSUER_URL="https://$SOAM_KC/auth/realms/$SOAM_KC_REALM_ID" \
  --from-literal=NATS_MAX_RECONNECT=60 \
  --from-literal=EMAIL_TEMPLATE_NOTIFY_SCHOOL_INCORRECT_FORMAT_FILE="<!DOCTYPE html><html xmlns:th=\"http://www.thymeleaf.org\"><head><meta charset=\"ISO-8859-1\"></head><body>Submission <span th:text=\"\${\$}{submissionNumber}\"></span> failed to load on <span th:text=\"\${\$}{dateTime}\"></span> because: <br><b th:text=\"\${\$}{failReason}\"></b> <br>Please correct and resend. <br>Regards <br><span th:text=\"\${\$}{fromEmail}\"></span></body></html>" \
  --from-literal=EMAIL_TEMPLATE_PEN_REQUEST_BATCH_ARCHIVE_HAS_SCHOOL_CONTACT="<!DOCTYPE html><html xmlns:th=\"http://www.thymeleaf.org\"><head><meta charset=\"ISO-8859-1\"></head><body>Your PEN WEB Request, submission <span th:text=\"\${\$}{submissionNumber}\"></span>, has been processed and the PEN Activity Report is available for download by going to <a th:href=\"@{\${\$}{penCoordinatorLoginUrl}}\" th:text=\"\${\$}{penCoordinatorLoginUrl}\"></a> and logging into the PEN Web System.</body></html>" \
  --from-literal=EMAIL_TEMPLATE_PEN_REQUEST_BATCH_ARCHIVE_HAS_NO_SCHOOL_CONTACT="<!DOCTYPE html><html xmlns:th=\"http://www.thymeleaf.org\"><head><meta charset=\"ISO-8859-1\"></head><body>The following message was NOT sent to <span th:text=\"\${\$}{mincode}\"></span> because there is NO email address in for this school/district in the PEN Coordinator table.<br><br>Your PEN WEB Request, submission <span th:text=\"\${\$}{submissionNumber}\"></span>, has been processed and is available by going to <a th:href=\"@{\${\$}{penCoordinatorLoginUrl}}\" th:text=\"\${\$}{penCoordinatorLoginUrl}\"></a> and logging into the PEN Web System.</body></html>" \
  --from-literal=EMAIL_SUBJECT_PEN_REQUEST_BATCH_ARCHIVE_HAS_SCHOOL_CONTACT="PEN Request Results for submission {0} School {1} {2} are Ready for Retrieval" \
  --from-literal=EMAIL_SUBJECT_PEN_REQUEST_BATCH_ARCHIVE_HAS_NO_SCHOOL_CONTACT="PEN Request Results for submission {0} Ready for Retrieval - {1} NOT IN PEN COORDINATOR TABLE" \
  --from-literal=URL_LOGIN_PEN_REQUEST_BATCH_PEN_COORDINATOR="https://www2.gov.bc.ca/gov/content/education-training/k-12/administration/program-management/pen" \
  --from-literal=NOTIFICATION_EMAIL_SWITCH_ON=true \
  --from-literal=EMAIL_TEMPLATE_PEN_REQUEST_BATCH_ARCHIVE_HAS_SCHOOL_CONTACT_ALL_PENDING="<!DOCTYPE html><html xmlns:th=\"http://www.thymeleaf.org\"><head><meta charset=\"ISO-8859-1\"></head><body>Your PEN WEB Request, submission <span th:text=\"\${\$}{submissionNumber}\"></span>, has not been processed due to errors. Please go to <a th:href=\"@{\${\$}{penCoordinatorLoginUrl}}\" th:text=\"\${\$}{penCoordinatorLoginUrl}\"></a> and log into the PEN Web System to review the PEN Request errors; the record data will need to be updated in the school's Student Information System (MyEducation typically) and resubmitted in a new batch file to the Ministry, in order to receive the requested PENs. If you do not understand the cause of error message, or if you are unsure how to correct the data, please contact the PENS.Coordinator@gov.bc.ca email.</body></html>" \
  --from-literal=EMAIL_TEMPLATE_PEN_REQUEST_BATCH_ARCHIVE_HAS_SCHOOL_CONTACT_SOME_PENDING="<!DOCTYPE html><html xmlns:th=\"http://www.thymeleaf.org\"><head><meta charset=\"ISO-8859-1\"></head><body>Your PEN WEB Request, submission <span th:text=\"\${\$}{submissionNumber}\"></span>, has been processed and the PEN Activity Report is available for download by going to <a th:href=\"@{\${\$}{penCoordinatorLoginUrl}}\" th:text=\"\${\$}{penCoordinatorLoginUrl}\"></a> and logging into the PEN Web System. <br><br>Please note that one or more pending PEN Requests require errors to be fixed by updating the data in the school's Student Information System (MyEducation typically) and resubmitting the request in a new Batch file. Alternatively, you may provide the requested additional information to the Ministry PEN Coordinator to have the file processed again.</body></html>" \
  --from-literal=EMAIL_SUBJECT_MACRO_CREATE="INSERT macro {0} in Dev, Test and UAT" \
  --from-literal=EMAIL_SUBJECT_MACRO_UPDATE="UPDATE macro {0} in Dev, Test and UAT" \
  --from-literal=EMAIL_TEMPLATE_MACRO_CREATE="<!DOCTYPE html><html xmlns:th=\"http://www.thymeleaf.org\"><head><meta charset=\"ISO-8859-1\"></head><body>New macro was created for <span th:text=\"\${\$}{businessUseTypeName}\"></span>:<br><br>Code: <span th:text=\"\${\$}{macroCode}\"></span><br>Type: <span th:text=\"\${\$}{macroTypeCode}\"></span><br>Text: <span th:text=\"\${\$}{macroText}\"></span></body></html>" \
  --from-literal=EMAIL_TEMPLATE_MACRO_UPDATE="<!DOCTYPE html><html xmlns:th=\"http://www.thymeleaf.org\"><head><meta charset=\"ISO-8859-1\"></head><body><span th:text=\"\${\$}{businessUseTypeName}\"></span> macro <span th:text=\"\${\$}{macroCode}\"></span> was updated to:<br><br>Code: <span th:text=\"\${\$}{macroCode}\"></span><br>Type: <span th:text=\"\${\$}{macroTypeCode}\"></span><br>Text: <span th:text=\"\${\$}{macroText}\"></span></body></html>" \
  --from-literal=PURGE_RECORDS_EVENT_AFTER_DAYS=365 \
  --from-literal=SCHEDULED_JOBS_PURGE_OLD_EVENT_RECORDS_CRON="@midnight" \
  --dry-run -o yaml | oc apply -f -

echo
echo Setting environment variables for "$APP_NAME-$SOAM_KC_REALM_ID" application
oc -n "$PEN_NAMESPACE-$envValue" set env --from="configmap/$APP_NAME-config-map" "dc/$APP_NAME-$SOAM_KC_REALM_ID"

echo Creating config map "$APP_NAME"-flb-sc-config-map
oc create -n "$PEN_NAMESPACE-$envValue" configmap "$APP_NAME-flb-sc-config-map" --from-literal="fluent-bit.conf=$FLB_CONFIG" --from-literal=parsers.conf="$PARSER_CONFIG" --dry-run -o yaml | oc apply -f -

echo Removing un-needed config entries
oc -n "$PEN_NAMESPACE"-"$envValue" set env "dc/$APP_NAME-$SOAM_KC_REALM_ID" SOAM_PUBLIC_KEY-
