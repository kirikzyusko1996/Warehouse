waitress-serve --port=8000 main:app

docker-compose up
docker container ls
docker stop :id

docker run -e MYSQL_ROOT_PASSWORD=root --name mysql -d -p=3306:3306 mysql
