javac -sourcepath src src/com/reengen/utils/auditreporter/Runner.java -d bin
java -cp bin com.reengen.utils.auditreporter.Runner resources/users.csv resources/files.csv 