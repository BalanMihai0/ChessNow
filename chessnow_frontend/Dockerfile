FROM node:18-alpine

WORKDIR /app

COPY package.json .

RUN npm install

COPY . .

RUN npm run build

EXPOSE 5180 

EXPOSE 8090

CMD [ "npm", "run", "dev" ]