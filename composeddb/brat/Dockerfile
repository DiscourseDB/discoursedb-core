# start from a base ubuntu image
FROM ubuntu
# Adapted from cassj/brat by Cass Johnston <cassjohnston@gmail.com>
# except I didn't want apache
MAINTAINER Chris Bogart <cbogart@cs.cmu.edu>

# set users cfg file
ARG USERS_CFG=users.json

# Install pre-reqs
RUN apt-get update
RUN apt-get install -y curl 
RUN apt-get install -y python
RUN apt-get clean
RUN rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

# Fetch  brat
RUN mkdir -p /var/www/brat
RUN curl http://weaver.nlplab.org/~brat/releases/brat-v1.3_Crunchy_Frog.tar.gz > /var/www/brat/brat-v1.3_Crunchy_Frog.tar.gz 
RUN cd /var/www/brat && tar -xvzf brat-v1.3_Crunchy_Frog.tar.gz
WORKDIR /var/www/brat/brat-v1.3_Crunchy_Frog/
ADD config.py /var/www/brat/brat-v1.3_Crunchy_Frog/config.py
ADD annotation.conf /var/www/brat/brat-v1.3_Crunchy_Frog/annotation.conf
RUN mkdir  /var/www/brat/brat-v1.3_Crunchy_Frog/data
RUN mkdir  /var/www/brat/brat-v1.3_Crunchy_Frog/work
RUN cd server/lib && tar xfz simplejson-2.1.5.tar.gz
RUN cp -r example-data/corpora data/examples
RUN cp -r example-data/tutorials data/tutorials

# create a symlink so users can mount their data volume at /bratdata rather than the full path
RUN groupadd -r bratuser
RUN useradd -g bratuser -ms /bin/bash bratuser
RUN chown -R bratuser:bratuser /var/www/brat/
RUN chmod -R 777 data work
RUN ln -s /var/www/brat/brat-v1.3_Crunchy_Frog/data /bratdata
RUN chown bratuser:bratuser /bratdata

# And make that location a volume
VOLUME /bratdata

EXPOSE 8001

#USER bratuser
CMD ["python","/var/www/brat/brat-v1.3_Crunchy_Frog/standalone.py"]
