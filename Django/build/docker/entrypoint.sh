#set( $userId = $aib.getParam("django.superUserId") )
#set( $email = $aib.getParam("django.superUserEmail") )
#set( $password = $aib.getParam("django.superUserPassword") )
#set( $port = $aib.getParam("django.port") )
#!/bin/bash

cd /var/www/
python manage.py migrate --run-syncdb
python manage.py shell -c "from django.contrib.auth import get_user_model; User = get_user_model(); User.objects.create_superuser('${userId}', '${email}', '${password}')"
python manage.py runserver 0.0.0.0:${port}