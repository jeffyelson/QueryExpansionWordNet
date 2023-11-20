@echo off

title Batch Script to start backend

echo Batch Script to start backend

cd "searchengine/"

./mvnw spring-boot:run

pause