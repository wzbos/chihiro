sdk use java 11.0.24-oracle
java -version
./gradlew clean
./gradlew publish
./gradlew jreleaserFullRelease --stacktrace