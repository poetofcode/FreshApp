FROM node:21-alpine
WORKDIR /home/poet/docker-pr/freshapp
COPY package*.json .
RUN npm install -g nodemon
RUN npm install
COPY . .
# config.json в .dockerignore (локальные настройки не должны попадать в образ),
# поэтому генерируем его из шаблона, если он отсутствует
RUN if [ ! -f config.json ]; then cp config_sample.json config.json; fi
ENV FreshApp_ENV=prod
# CMD ["npm", "start"]
CMD ["npm", "run", "dev"]