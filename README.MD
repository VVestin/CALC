# CALC
The __C__ompiled __A__lternative __Language__ for __C__alculator Programming was created as a replacement for TI-BASIC, the built-in, interpreted programming language for TI-84 series calculators.
This is a Java-based compiler that takes CALC code as input and outputs a TI-84 program file (.8xp) file that can be executed on a calculator.

### The Project
See this informational poster: [poster.pdf](http://vvest.in/poster.pdf)

### A code sample
```
Fun FACT(N)
   If N=1
      Return 1
   Else
      Return N*FACT(N-1)
   End
End

0->E
For(A,1,100)
   E+1/FACT(A)->E
End

ClrHome
OutputS(1,1,"The answer is:")
Output(2,1,E)
```
