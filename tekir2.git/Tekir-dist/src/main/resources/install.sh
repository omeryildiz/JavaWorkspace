# Tekir ${project.version} Kurulum Betigi

cp tekir-ear.ear $1/server/default/deploy
cp conf/tekir-ds.xml $1/server/default/deploy
cp conf/tekir-mail-service.xml $1/server/default/deploy
cp conf/tekir.properties $1/server/default/conf
cp conf/mysql-connector-java-5.1.6-bin.jar $1/server/default/lib

echo $1/server/default/deploy/tekir-ds.xml içerisinde veri tabanı yolunu düzeltiniz
echo $1/server/default/deploy/tekir-mail-service.xml içerisinde e-posta sunucu bilgilerini düzeltiniz
echo veri tabanı kurulumunu yapınız