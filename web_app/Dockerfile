FROM node:23-alpine3.20 AS deps

WORKDIR /app

COPY package*.json ./

RUN npm ci

FROM node:23-alpine3.20 AS build

WORKDIR /app

COPY --from=deps /app/node_modules ./node_modules

COPY . .

RUN npm run build

FROM node:23-alpine3.20 AS prod-deps

WORKDIR /app

COPY package*.json ./

RUN npm ci --only=production

FROM node:23-alpine3.20 AS prod

ENV NODE_ENV=production

WORKDIR /app

COPY --from=prod-deps /app/node_modules ./node_modules
COPY --from=build /app/package*.json ./
COPY --from=build /app/public ./public
COPY --from=build /app/.next ./.next

CMD [ "npm", "start" ]
