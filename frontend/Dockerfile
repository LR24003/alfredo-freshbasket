FROM nginx:1.31.1-alpine

# Copiar los archivos estáticos (Esto ya funciona)
COPY ./dist /usr/share/nginx/html

# CAMBIO AQUÍ: Apuntamos solo a la carpeta terminando con una barra "/"
COPY nginx.conf /etc/nginx/conf.d/

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]