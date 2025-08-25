
classes="Framework-Spring"
archive="${classes}.jar"
rm -rf Framework-Spring

for file in src/*; do
     find . -name "*.java" -exec javac -parameters -cp .:/Users/hedyhamael/lib_jar/jakarta.servlet-api-6.1.0-M2.jar:/Users/hedyhamael/lib_jar/gson-2.11.0.jar:/Users/hedyhamael/lib_jar/jsoup-1.17.2.jar -d $classes {} +
done

jar cf $archive -C Framework-Spring/ .
rm -R Framework-Spring   

