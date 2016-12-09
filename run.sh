javac -d bin -cp bin src/in/vvest/generator/Generator.java
javac -d bin -cp bin src/in/vvest/lexer/Lexer.java
javac -d bin -cp bin src/in/vvest/lexer/TI84Token.java
javac -d bin -cp bin src/in/vvest/lexer/Token.java
javac -d bin -cp bin src/in/vvest/lexer/TokenClass.java
javac -d bin -cp bin src/in/vvest/main/Main.java
javac -d bin -cp bin src/in/vvest/parser/MarkedToken.java
javac -d bin -cp bin src/in/vvest/parser/Parser.java
javac -d bin -cp bin src/in/vvest/parser/TreeNode.java

cd bin
java in.vvest.main.Main
cd ..
