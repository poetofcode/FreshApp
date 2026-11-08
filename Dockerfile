FROM node:21-alpine
WORKDIR /home/poet/docker-pr/freshapp
COPY package*.json .
RUN npm install -g nodemon
RUN npm install
COPY . .
# config.json копируется в образ из контекста сборки (исключён в .dockerignore),
# при его отсутствии генерируем файл из шаблона
RUN if [ ! -f config.json ]; then cp config_sample.json config.json; fi
ENV FreshApp_ENV=prod
# CMD ["npm", "start"]
CMD ["npm", "run", "dev"]
