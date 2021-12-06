#set( $appName = ${aib.getApplicationNameFormatted()} )
FROM #Ruby_Image()

RUN apt-get update && \
    apt-get install -y build-essential libcurl4-openssl-dev libxml2-dev libsqlite3-dev libpq-dev npm postgresql-client sqlite3 --no-install-recommends && \ 
    apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

ENV APP_PATH /usr/src/app/

RUN mkdir -p $APP_PATH

ADD ./${appName} $APP_PATH

WORKDIR $APP_PATH

RUN ls

RUN bundle install

ENV RAILS_ENV development
RUN npm install --global yarn
RUN rake webpacker:compile

RUN rake db:migrate 
RUN rake db:seed

EXPOSE 3000

CMD ["bundle", "exec", "rails", "server", "-b", "0.0.0.0"]CMD ["bundle", "exec", "rails", "server", "-b", "0.0.0.0"]
