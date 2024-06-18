set "jdk_path=C:\Program Files\Java\jdk-17"

mkdir /s /q tempjava
mkdir javacompiler
mkdir tempjava
for /r src %%f in (*.java) do (
    copy /y "%%f" tempjava\
)

"%jdk_path%\bin\javac" -parameters -d javacompiler tempjava\*.java
"%jdk_path%\bin\jar" cf framework-ETU002500.jar -C javacompiler\ .