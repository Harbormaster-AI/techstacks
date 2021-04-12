#set( $userId = $aib.getParam("application.adminUserId") )
#set( $email = $aib.getParam("application.email") )
#set( $password = $aib.getParam("application.adminPassword") )
#!/bin/bash

cd /var/www/
python manage.py migrate --run-syncdb
python manage.py shell -c "from django.contrib.auth import get_user_model; User = get_user_model(); User.objects.create_superuser('${userId}', '${email}', '${password}')"
python manage.py runserver 0.0.0.0:8080