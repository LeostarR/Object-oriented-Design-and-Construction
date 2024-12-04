### Introduction

The overall training objective of the first unit assignments is to model expression structures, experience hierarchical design thinking, and develop iteratively. The three assignments focus on expanding polynomial expressions in the first task, introducing trigonometric functions (sin, cos) and custom functions (f(x), g(x,y), h(x,y,z)) in the second task, and adding differentiation factors (dx(expression)) and allowing function definitions to call other predefined functions in the third task.

You can refer to my code here: [GitHub Repository](https://github.com/LeostarR/Object-oriented-Design-and-Construction)

## Summary of First Assignment

### Design and Architecture

My general approach was divided into three steps:

1. Preprocessing the expression
2. Parsing the expression
3. Converting to a string and printing

#### Preprocessing Expressions

##### `PreStr`

![](https://pic.superbed.cc/item/66ab4785fcada11d3763e8af.png)

Preprocessing includes:

- Removing all consecutive whitespace characters using Java's built-in `replaceAll` method.
- Using a series of `replaceAll` calls to ensure that the string does not contain two or more consecutive '+' or '-' signs (e.g., "----" should be simplified to "+", "+-" to "-").
- Replacing the power symbol "**" with "^" to avoid confusion with multiplication symbols and prevent unnecessary bugs.

#### Parsing Expressions

##### `Lexer`

Processing involves:

![](https://pic.superbed.cc/item/66ab47a8fcada11d3763ebe4.png)

- `read()` ensures that each operand or operator is retrieved at a time.
- `next()` reads the next operand or operator.
- Due to the unpredictable length of numbers, `getNumber()` retrieves the current number.

##### `Ele`

According to the requirements, I created an `Ele` class for storing basic elements. Each `Ele` has the following structure:

![](https://pic.superbed.cc/item/66ab47c1fcada11d3763ed2a.png)

![](https://pic.superbed.cc/item/66ab48f9fcada11d376494b9.png)

- `coe` stores the coefficient of each `Ele`.![](https://pic.superbed.cc/item/66ab47eafcada11d3763f18d.png)
- `hashVar` uses a `HashMap` to store variables and their corresponding powers.

Initially, only `setCoe()`, `getCoe()`, `setHashVar()`, and `getHashVar()` methods were written for setting and getting values. Other methods were added during the overall framework construction.

##### `Expr`

A sequence of expressions looks like this:



Coefficients may carry signs themselves, so they are directly connected by plus signs.

To facilitate data access, I chose an `ArrayList` to store multiple `Ele` objects:

The `Expr` class contains methods such as `initExperList(ArrayList<Ele>)`, `initExpr(Ele)` for initialization, and `getExperlist()` for retrieving the `ArrayList`. Many other methods were completed during the overall construction process.

##### `Parser`

Using recursive descent parsing for expressions:

![](https://pic.superbed.cc/item/66ab4865fcada11d376426ed.png)

Note: `dealEx(Expr)` is part of `parseExpr()`, separated due to method length affecting code style.

1. `parseExpr()` parses expressions, which consist of several terms connected by addition and subtraction operators. The first term may have a positive or negative sign indicating its polarity. Thus, it considers the following structure:



```java
public parseExpr() {
    if (currentSymbol == '+' || currentSymbol == '-') { // An expression's first term can have a sign (+, -)
        parseTerm();
        combTerm();
    } else {
        parseTerm();
        combTerm();
    }
    while (currentSymbol == '+' || currentSymbol == '-') {
        parseTerm();
        combTerm();
    }
    return expr;
}
```

Sign handling is involved, so distinguishing between addition and subtraction within the method isn't necessary; instead, a new combination method `Expr.combTerm(ArrayList<Ele> list, String s)` is used, where `s` represents the sign. If `s` is a minus sign, `Expr.reverseList(ArrayList<Ele> list)` is called to negate, then `addAll()` is used. For performance, simplification occurs after `combTerm` using `merge()` to combine like terms.

1. `parseTerm()` parses terms, which consist of several factors connected by multiplication operators. Similar to above but requires writing a multiplication method `Expr.mulTerm(ArrayList<Ele>)` to multiply two `ArrayList<Ele>` objects (essentially multiple additions).
2. `parseFactor()` parses factors, which include power functions, constant factors, and expression factors.
3. `parsePow()` handles power functions similarly and returns an `Expr` object. Separated due to method length affecting code style.

#### Converting to String and Printing

This primarily manifests in the `toString()` method of the `Expr` class. The idea is to set up a `StringBuilder` and continuously append. Each `Ele` in the `ArrayList` is sequentially checked for '+', whether it is ±1, if the exponent is 0, and if the whole expression is 0. This section requires special attention to detail.

### Program Structure Analysis Based on Metrics

#### Code Size Analysis

Core code is around 450 lines, with the `Expr` class having the most lines at 175, followed by the `Parser` class with 119 lines.

![](https://pic.superbed.cc/item/66ab4887fcada11d37644047.png)

#### Complexity Analysis

##### Method Complexity

| Method                                            | CogC | ev(G) | iv(G) | v(G) |
| :------------------------------------------------ | :--- | :---- | :---- | :--- |
| DeFun.max(int, int)                               | 0    | 1     | 1     | 1    |
| DeFun.process(String)                             | 22   | 6     | 13    | 13   |
| DeFun.readFun(String)                             | 6    | 3     | 5     | 6    |
| Lexer.Lexer(String)                               | 0    | 1     | 1     | 1    |
| Lexer.getNumber()                                 | 2    | 1     | 3     | 3    |
| Lexer.next()                                      | 5    | 2     | 3     | 6    |
| Lexer.read()                                      | 0    | 1     | 1     | 1    |
| MainClass.main(String[])                          | 2    | 1     | 3     | 3    |
| Parser.Parser(Lexer)                              | 0    | 1     | 1     | 1    |
| Parser.dealEx(Expr)                               | 0    | 1     | 1     | 1    |
| Parser.parseExpr()                                | 5    | 1     | 5     | 5    |
| Parser.parseFactor()                              | 18   | 5     | 11    | 12   |
| Parser.parsePow()                                 | 2    | 1     | 2     | 2    |
| Parser.parseTerm()                                | 9    | 1     | 6     | 7    |
| Parser.parseTri()                                 | 8    | 1     | 4     | 4    |
| PreStr.pre(String)                                | 6    | 1     | 6     | 7    |
| expr.Ele.Ele()                                    | 0    | 1     | 1     | 1    |
| expr.Ele.canMerge(Ele)                            | 4    | 1     | 4     | 6    |
| expr.Ele.compareTo(Object)                        | 27   | 13    | 12    | 13   |
| expr.Ele.getCoe()                                 | 0    | 1     | 1     | 1    |
| expr.Ele.getHashCos()                             | 0    | 1     | 1     | 1    |
| expr.Ele.getHashSin()                             | 0    | 1     | 1     | 1    |
| expr.Ele.getHashVar()                             | 0    | 1     | 1     | 1    |
| expr.Ele.initHashCos(HashMap<String, BigInteger>) | 0    | 1     | 1     | 1    |
| expr.Ele.initHashSin(HashMap<String, BigInteger>) | 0    | 1     | 1     | 1    |
| expr.Ele.initHashVar(HashMap<String, BigInteger>) | 0    | 1     | 1     | 1    |
| expr.Ele.isOne()                                  | 6    | 1     | 5     | 8    |
| expr.Ele.isZero()                                 | 0    | 1     | 1     | 1    |
| expr.Ele.putHashCos(HashMap<String, BigInteger>)  | 4    | 1     | 3     | 3    |
| expr.Ele.putHashSin(HashMap<String, BigInteger>)  | 4    | 1     | 3     | 3    |
| expr.Ele.setCoe(BigInteger)                       | 0    | 1     | 1     | 1    |
| expr.Ele.setHashCos(String, BigInteger)           | 0    | 1     | 1     | 1    |
| expr.Ele.setHashSin(String, BigInteger)           | 0    | 1     | 1     | 1    |
| expr.Ele.setHashVar(String, BigInteger)           | 0    | 1     | 1     | 1    |
| expr.Ele.zero()                                   | 0    | 1     | 1     | 1    |
| expr.Expr.combTerm(ArrayList, String)             | 1    | 1     | 2     | 2    |
| expr.Expr.getExprList()                           | 0    | 1     | 1     | 1    |
| expr.Expr.initExpr(Ele)                           | 0    | 1     | 1     | 1    |
| expr.Expr.initExprList(ArrayList)                 | 0    | 1     | 1     | 1    |
| expr.Expr.merge()                                 | 10   | 4     | 7     | 7    |
| expr.Expr.mulTerm(ArrayList)                      | 3    | 1     | 3     | 3    |
| expr.Expr.reverse()                               | 1    | 1     | 2     | 2    |
| expr.Expr.reverseList(ArrayList)                  | 1    | 1     | 2     | 2    |
| expr.Expr.simplify()                              | 4    | 3     | 4     | 4    |
| expr.Expr.sort()                                  | 32   | 13    | 12    | 13   |
| expr.Expr.toString()                              | 16   | 1     | 10    | 11   |
| expr.Expr.triStr(Ele)                             | 14   | 1     | 11    | 11   |
| expr.Expr.varStr(Ele)                             | 21   | 1     | 16    | 16   |
| expr.Term.getTermList()                           | 0    | 1     | 1     | 1    |
| Total                                             | 233  | 90    | 180   | 195  |
| Average                                           | 4.75 | 1.83  | 3.67  | 3.97 |

##### Class Complexity

![](https://pic.superbed.cc/item/66ab4967fcada11d3764e765.png)

The diagram shows that the most complex methods are `Parser.parseFactor()`, `expr.Expr.toString()`, and `expr.Expr.varStr(Ele)`. The complexity increases significantly for these methods because they handle multiple types of factors and string conversion scenarios.

The diagram also highlights that the `Expr`, `Ele`, and `Parser` classes have higher complexity due to the numerous methods for decomposition and calculation/merging.

###### Attached: Metric Analysis Item Explanations

- CogC: Cognitive Complexity
- ev(G): Basic complexity of non-abstract methods, measures control flow structure defects, range [1, v(G)]
- iv(G): Design complexity of methods, measures coupling between method control flows and other methods, range [1, v(G)]
- v(G): Cyclomatic complexity of non-abstract methods, measures the number of different execution paths within each method
- OC: Non-abstract method cyclomatic complexity of classes, excluding inherited classes
- WMC: Total cyclomatic complexity of classes

#### UML Class Diagram

Each class design consideration has been explained in the previous sections, so it will not be repeated here.

![](https://pic.superbed.cc/item/66ab48bafcada11d37646547.jpg)

## Summary of Second Assignment

### Design and Architecture

New requirements for this assignment included:

- Supporting nested multi-level parentheses.
- Adding trigonometric factor support, with any factor inside trigonometric function parentheses.
- Introducing custom function factors, though custom function expressions do not call other functions.

Based on the first assignment, the following tasks needed to be accomplished:

1. Replace custom functions.
2. Introduce new data structures to store trigonometric factors.
3. Add or improve methods to complete trigonometric function calculations and simplifications.

#### Replacing Custom Functions

##### `DeFun`

Implementation of replacement allows for expression parsing using the previously implemented recursive descent:

![](https://pic.superbed.cc/item/66ab48d0fcada11d376472dd.png)

- Splitting the string by '=' to find formal parameters from the left side and storing them along with the right-side expression in a `HashMap`.
- Replacing all custom functions in the original expression through `process()`.

#### Handling Trigonometric Factors

##### `Ele`

Each `Ele` type now looks like this:

![](https://pic.superbed.cc/item/66ab48e7fcada11d376484ba.png)

Trigonometric factors inside parentheses are converted to strings as keys, with exponents as values stored in a `HashMap`.

Methods for handling trigonometric factors have been added:

![](https://pic.superbed.cc/item/66ab48f9fcada11d376494b9.png)

##### `Expr`

Addition and multiplication require only adding trigonometric factor operations on top of existing logic (addition and multiplication need additional conditions in `canMerge()`).

![](https://pic.superbed.cc/item/66ab492efcada11d3764be2f.png)

In `toString()`, a method `triStr(Ele)` for printing trigonometric factors is added.

### Program Structure Analysis Based on Metrics

#### Code Size Analysis

Core code is around 742 lines, with the `Expr` class having the most lines at 259, followed by the `Parser` class with 150 lines.

![](https://pic.superbed.cc/item/66ab494cfcada11d3764d335.png)

#### Complexity Analysis

##### Method Complexity

| Method                                            | CogC | ev(G) | iv(G) | v(G) |
| :------------------------------------------------ | :--- | :---- | :---- | :--- |
| DeFun.max(int, int)                               | 0    | 1     | 1     | 1    |
| DeFun.process(String)                             | 22   | 6     | 13    | 13   |
| DeFun.readFun(String)                             | 6    | 3     | 5     | 6    |
| Lexer.Lexer(String)                               | 0    | 1     | 1     | 1    |
| Lexer.getNumber()                                 | 2    | 1     | 3     | 3    |
| Lexer.next()                                      | 5    | 2     | 3     | 6    |
| Lexer.read()                                      | 0    | 1     | 1     | 1    |
| MainClass.main(String[])                          | 2    | 1     | 3     | 3    |
| Parser.Parser(Lexer)                              | 0    | 1     | 1     | 1    |
| Parser.dealEx(Expr)                               | 0    | 1     | 1     | 1    |
| Parser.parseExpr()                                | 5    | 1     | 5     | 5    |
| Parser.parseFactor()                              | 18   | 5     | 11    | 12   |
| Parser.parsePow()                                 | 2    | 1     | 2     | 2    |
| Parser.parseTerm()                                | 9    | 1     | 6     | 7    |
| Parser.parseTri()                                 | 8    | 1     | 4     | 4    |
| PreStr.pre(String)                                | 6    | 1     | 6     | 7    |
| expr.Ele.Ele()                                    | 0    | 1     | 1     | 1    |
| expr.Ele.canMerge(Ele)                            | 4    | 1     | 4     | 6    |
| expr.Ele.compareTo(Object)                        | 27   | 13    | 12    | 13   |
| expr.Ele.getCoe()                                 | 0    | 1     | 1     | 1    |
| expr.Ele.getHashCos()                             | 0    | 1     | 1     | 1    |
| expr.Ele.getHashSin()                             | 0    | 1     | 1     | 1    |
| expr.Ele.getHashVar()                             | 0    | 1     | 1     | 1    |
| expr.Ele.initHashCos(HashMap<String, BigInteger>) | 0    | 1     | 1     | 1    |
| expr.Ele.initHashSin(HashMap<String, BigInteger>) | 0    | 1     | 1     | 1    |
| expr.Ele.initHashVar(HashMap<String, BigInteger>) | 0    | 1     | 1     | 1    |
| expr.Ele.isOne()                                  | 6    | 1     | 5     | 8    |
| expr.Ele.isZero()                                 | 0    | 1     | 1     | 1    |
| expr.Ele.putHashCos(HashMap<String, BigInteger>)  | 4    | 1     | 3     | 3    |
| expr.Ele.putHashSin(HashMap<String, BigInteger>)  | 4    | 1     | 3     | 3    |
| expr.Ele.setCoe(BigInteger)                       | 0    | 1     | 1     | 1    |
| expr.Ele.setHashCos(String, BigInteger)           | 0    | 1     | 1     | 1    |
| expr.Ele.setHashSin(String, BigInteger)           | 0    | 1     | 1     | 1    |
| expr.Ele.setHashVar(String, BigInteger)           | 0    | 1     | 1     | 1    |
| expr.Ele.zero()                                   | 0    | 1     | 1     | 1    |
| expr.Expr.combTerm(ArrayList, String)             | 1    | 1     | 2     | 2    |
| expr.Expr.getExprList()                           | 0    | 1     | 1     | 1    |
| expr.Expr.initExpr(Ele)                           | 0    | 1     | 1     | 1    |
| expr.Expr.initExprList(ArrayList)                 | 0    | 1     | 1     | 1    |
| expr.Expr.merge()                                 | 10   | 4     | 7     | 7    |
| expr.Expr.mulTerm(ArrayList)                      | 3    | 1     | 3     | 3    |
| expr.Expr.reverse()                               | 1    | 1     | 2     | 2    |
| expr.Expr.reverseList(ArrayList)                  | 1    | 1     | 2     | 2    |
| expr.Expr.simplify()                              | 4    | 3     | 4     | 4    |
| expr.Expr.sort()                                  | 32   | 13    | 12    | 13   |
| expr.Expr.toString()                              | 16   | 1     | 10    | 11   |
| expr.Expr.triStr(Ele)                             | 14   | 1     | 11    | 11   |
| expr.Expr.varStr(Ele)                             | 21   | 1     | 16    | 16   |
| expr.Term.getTermList()                           | 0    | 1     | 1     | 1    |
| Total                                             | 233  | 90    | 180   | 195  |
| Average                                           | 4.75 | 1.83  | 3.67  | 3.97 |

### Class Complexity



![Class Complexity](https://pic.superbed.cc/item/66ab4967fcada11d3764e765.png)



#### UML Class Diagram



![UML Class Diagram](https://pic.superbed.cc/item/66ab4989fcada11d37650132.jpg)



The design considerations for each class have been explained in the previous sections on design and architecture, so they will not be repeated here.

## Summary of Third Assignment

### Design and Architecture

For this assignment, new requirements were introduced:

- Custom function definitions can now call other predefined custom functions.
- A differentiation factor has been added.

Based on the second assignment, the following tasks needed to be completed:

1. Improve methods in the `DeFun` class to support calling previously defined custom functions and include differentiation factors.
2. Add a differentiation method in the `Expr` class.

#### Improving the Replacement of Custom Functions

##### `DeFun`

Replace custom functions that already exist in the HashMap within the same class, then replace the differentiation factors (according to the assignment requirements, simplification and replacement of differentiation factors need to occur at this stage). Therefore, we directly reuse the Parser class to parse the expression inside the differentiation factor and use `toString()` to replace the original differentiation factor.

Below is an example snippet:

```java
if (str.contains("d")) { // Differentiation factor appears only once
    int begin = str.indexOf("d");
    // subString = dx(expression) | dy(expression) | dz(expression), which is the differentiation factor
    String subString = sim.substring(/* extract the substring inside dx() or dy() or dz() */);
    String derFact = subString.substring(2); // derFact is the expression to differentiate
    parser.parseExpr(); // Preprocess, parse, return an Expr object
    expr.derExpr(); // Differentiate the expression inside the parentheses, returning an Expr object
    str = str.replace(subString, "(" + expr + ")"); // Remove (replace) the entire differentiation factor
}
```

#### Adding Differentiation Methods

##### `Ele`

In the `Ele` class, add methods to check if it contains a specific variable ('x', 'y', 'z'). If it does not contain the variable, the result of differentiation should be zero; otherwise, perform normal differentiation.

![](https://pic.superbed.cc/item/66ab49b0fcada11d37651f94.png)

##### `Expr`

Add support for differentiation methods where `derExpr(char)` differentiates with respect to the parameter `char` as the independent variable, separately differentiating each `Ele`'s variable part and trigonometric factor part before combining them.

![](https://pic.superbed.cc/item/66ab49c5fcada11d37652fa1.png)

### Program Structure Analysis Based on Metrics

#### Code Size Analysis

Core code is around 899 lines, with the `Expr` class having the most lines at 370, followed by the `Parser` class with 173 lines.

![](https://pic.superbed.cc/item/66ab49dcfcada11d376541d1.png)

#### Complexity Analysis

##### Method Complexity

| Method                                       | CogC | ev(G) | iv(G) | v(G) |
| :------------------------------------------- | :--- | :---- | :---- | :--- |
| DeFun.findRi(int, String)                    | 9    | 5     | 6     | 6    |
| DeFun.max(int, int)                          | 0    | 1     | 1     | 1    |
| DeFun.process(String)                        | 10   | 1     | 8     | 8    |
| DeFun.readFun(String)                        | 7    | 3     | 6     | 7    |
| Ele.Ele()                                    | 0    | 1     | 1     | 1    |
| Ele.canMerge(Ele)                            | 4    | 1     | 4     | 6    |
| Ele.compareTo(Object)                        | 27   | 13    | 12    | 13   |
| Ele.cosContain(String)                       | 3    | 3     | 2     | 3    |
| Ele.getCoe()                                 | 0    | 1     | 1     | 1    |
| Ele.getHashCos()                             | 0    | 1     | 1     | 1    |
| Ele.getHashSin()                             | 0    | 1     | 1     | 1    |
| Ele.getHashVar()                             | 0    | 1     | 1     | 1    |
| Ele.initHashCos(HashMap<String, BigInteger>) | 0    | 1     | 1     | 1    |
| Ele.initHashSin(HashMap<String, BigInteger>) | 0    | 1     | 1     | 1    |
| Ele.initHashVar(HashMap<String, BigInteger>) | 0    | 1     | 1     | 1    |
| Ele.isOne()                                  | 6    | 1     | 5     | 8    |
| Ele.isZero()                                 | 0    | 1     | 1     | 1    |
| Ele.putHashCos(HashMap<String, BigInteger>)  | 4    | 1     | 3     | 3    |
| Ele.putHashSin(HashMap<String, BigInteger>)  | 4    | 1     | 3     | 3    |
| Ele.setCoe(BigInteger)                       | 0    | 1     | 1     | 1    |
| Ele.setHashCos(String, BigInteger)           | 0    | 1     | 1     | 1    |
| Ele.setHashSin(String, BigInteger)           | 0    | 1     | 1     | 1    |
| Ele.setHashVar(String, BigInteger)           | 0    | 1     | 1     | 1    |
| Ele.sinContain(String)                       | 3    | 3     | 2     | 3    |
| Ele.varContain(String)                       | 1    | 1     | 1     | 2    |
| Ele.zero()                                   | 0    | 1     | 1     | 1    |
| Expr.combTerm(ArrayList, String)             | 1    | 1     | 2     | 2    |
| Expr.derCos(int, String)                     | 7    | 1     | 5     | 5    |
| Expr.derExpr(char)                           | 14   | 1     | 8     | 8    |
| Expr.derSin(int, String)                     | 7    | 1     | 5     | 5    |
| Expr.derVar(int, String)                     | 0    | 1     | 1     | 1    |
| Expr.getExprList()                           | 0    | 1     | 1     | 1    |
| Expr.initExpr(Ele)                           | 0    | 1     | 1     | 1    |
| Expr.initExprList(ArrayList)                 | 0    | 1     | 1     | 1    |
| Expr.merge()                                 | 10   | 4     | 7     | 7    |
| Expr.mulTerm(ArrayList)                      | 3    | 1     | 3     | 3    |
| Expr.reverse()                               | 1    | 1     | 2     | 2    |
| Expr.reverseList(ArrayList)                  | 1    | 1     | 2     | 2    |
| Expr.simplify()                              | 4    | 3     | 4     | 4    |
| Expr.sort()                                  | 32   | 13    | 12    | 13   |
| Expr.toString()                              | 16   | 1     | 10    | 11   |
| Expr.triStr(Ele)                             | 22   | 1     | 13    | 13   |
| Expr.varStr(Ele)                             | 21   | 1     | 16    | 16   |
| Lexer.Lexer(String)                          | 0    | 1     | 1     | 1    |
| Lexer.getNumber()                            | 2    | 1     | 3     | 3    |
| Lexer.next()                                 | 6    | 2     | 6     | 7    |
| Lexer.read()                                 | 0    | 1     | 1     | 1    |
| MainClass.main(String[])                     | 2    | 1     | 3     | 3    |
| Parser.Parser(Lexer)                         | 0    | 1     | 1     | 1    |
| Parser.dealEx(Expr)                          | 0    | 1     | 1     | 1    |
| Parser.parseDer(char)                        | 0    | 1     | 1     | 1    |
| Parser.parseExpr()                           | 5    | 1     | 5     | 5    |
| Parser.parseFactor()                         | 20   | 6     | 14    | 15   |
| Parser.parsePow()                            | 2    | 1     | 2     | 2    |
| Parser.parseTerm()                           | 9    | 1     | 6     | 7    |
| Parser.parseTri()                            | 8    | 1     | 4     | 4    |
| PreStr.pre(String)                           | 6    | 1     | 6     | 7    |
| Total                                        | 277  | 102   | 214   | 230  |
| Average                                      | 4.85 | 1.79  | 3.75  | 4.04 |

##### Class Complexity



![Class Complexity](https://pic.superbed.cc/item/66ab49f4fcada11d3765538f.png)



The diagram shows that the `Expr`, `Ele`, and `Parser` classes have higher complexity due to the numerous simplification, calculation, and differentiation methods within the `Expr` class.

#### UML Class Diagram



![UML Class Diagram](https://pic.superbed.cc/item/66ab4a0bfcada11d37656557.jpg)



The design considerations for each class have been explained in the previous sections on design and architecture, so they will not be repeated here.

## Bug Analysis

### Analyzing Bugs in My Program

##### First Assignment

- Special cases such as `0^0+5^0+2*10^0` required debugging and modifications in `toString()`.

##### Second Assignment

- When replacing custom functions based on commas, there could be errors when handling nested functions like `f(g(y,z),h(x,y),sin(x))` with more than two commas. The solution was to find and replace the last custom function in each loop (ensuring no nested custom functions are inside).
- Pay attention to index positions during replacement, e.g., `f((((x,y,z))))` might cause bracket judgment errors.
- Handle special and edge cases, such as `sin((0))^2`, `sin((0))^0`, `cos((0))^3`.
- When the exponent is 0, directly read the integer rather than parsing the subsequent expression.

##### Third Assignment

- Simplification after differentiation caused issues, e.g., `dx(x*cos(x)**0)` would result in incorrect output even though the parsed `Expr` was correct. This was because checking if the cosine part contains `x` relied on whether the `HashMap` was empty (leftover from the second assignment), but I did not remove entries accordingly after differentiation, leading to incorrect output.

### Analyzing Bugs in Others' Programs

Manually construct representative boundary data with multiple levels of nested parentheses and special conditions (results being 0, 1, or no independent variable inside the differentiation), such as:

```javascript
1
f(x,y)=(((sin(cos(x))-(+1-sin(cos(y))))))
f((sin(x)),(y**2-0**0))+cos(0)**0-sin(0)**0
0
dx(x*cos(x)**0)
```

## Reflection

The first unit assignments focused on iterative development of expanding expressions and differentiation over three iterations. Initially struggling with understanding the role of Git, I gradually recognized the importance of version control in projects. From feeling lost with IDEA and making frequent mistakes to appreciating its powerful features, and from unfamiliarity with Java to becoming acquainted with its many convenient methods, data structures, and object-oriented thinking. Additionally, the emphasis on coding style has made me increasingly aware of its importance and improved my programming style. The first assignment was challenging due to the lack of knowledge about Java syntax and data structures. Iterative development in the second and third assignments was easier than starting from scratch, but extra care had to be taken to minimize coupling between classes and methods, avoiding changes to existing methods to make bug detection easier. Lastly, reviewing classmates' design ideas in discussion areas greatly assisted my architectural design. Thank you to the teaching assistants and fellow students for your enthusiastic help!

---



## 介绍

第一单元作业总的训练目标是对表达式结构进行建模，体会层次化的思想，三次作业为迭代开发。第一次作业是完成对多项式括号的展开，第二次作业新引入了三角函数(sin,cos)以及自定义函数(f(x),g(x,y),h(x,y,z))，第三次作业新增求导因子(dx(表达式))并允许函数定义时调用其他已定义过的函数。

点击此处可以参考我的代码：https://github.com/LeostarR/Object-oriented-Design-and-Construction

## 第一次作业总结

### 设计与架构

我的总思路分为三步：

1. 预处理表达式
2. 解析表达式
3. 转化为字符串并打印

#### 预处理表达式

##### `PreStr`

预处理：

![](https://pic.superbed.cc/item/66ab4785fcada11d3763e8af.png)

- 去除所有连续的空白字符（用Java自带的方法`replaceAll`即可解决）
- 通过一系列`replaceAll`使得字符串不含有连续的两个及以上的'+'和'-'（例如"----"应化为"+"，"+-"应化为"-"）
- 将表示幂次的"**"替换为"^"，防止与乘号弄混，产生不必要的bug

#### 解析表达式

##### `Lexer`

读取处理：

![](https://pic.superbed.cc/item/66ab47a8fcada11d3763ebe4.png)

- `read()`能够保证每次取出当前的操作数或者运算符
- `next()`能够往后读一个操作数或者运算符
- 由于数字长度的不确定，使用`getNumber()`能够取出当前数字

##### `Ele`

根据题目要求，我选择新建一个`Ele`类完成对基本元素的存储，每一个`Ele`应形如：

![](https://pic.superbed.cc/item/66ab47c1fcada11d3763ed2a.png)

![](https://pic.superbed.cc/item/66ab47dbfcada11d3763f065.png)

`coe`存储每一个`Ele`的系数，`hashVar`通过`HashMap`存储变量及其对应幂次。

![](https://pic.superbed.cc/item/66ab47eafcada11d3763f18d.png)

刚开始写`Ele`时只写了`setCoe()`,`getCoe()`,`setHashVar()`,`getHashVar()`四个方法用于设定值和取值，其他方法是在后续框架的整体搭建中书写的，因此放到后面讲解。

##### `Expr`

一连串的表达式形如：

![](https://pic.superbed.cc/item/66ab480ffcada11d3763f56f.png)

此处系数本身是可能带符号的，因此直接用加号相连。

为了方便数据的调用，我选择`ArrayList`来存储多个`Ele`：

![](https://pic.superbed.cc/item/66ab4831fcada11d3763fee0.png)

`Expr`方法中，`initExperList(ArrayList<Ele>)`,`initExpr(Ele)`用于初始化，`getExperlist()`用于取出该`ArrayList`，其他很多方法是在构建整体的过程中完成的，因此也放到后面。

![](https://pic.superbed.cc/item/66ab484cfcada11d3764139a.png)

##### `Parser`

使用递归下降解析表达式：

![](https://pic.superbed.cc/item/66ab4865fcada11d376426ed.png)

注：`dealEx(Expr)`是`parseExpr()`的一部分，分离出来是因为方法过长影响码风:）

1.`parseExpr()`用于解析表达式，根据定义，表达式由加法和减法运算符连接若干项组成并且在第一项之前，可以带一个正号或者负号，表示第一个项的正负，因此考虑如下结构。

```java
public parseExpr() {
        if (当前符号 == '+' || 当前符号 == '-') { //表达式的第一个项可以有符号（+，-）
            parseTerm();
            combTerm();
        } else {
            parseTerm();
            combTerm();
        }
        while (当前符号 == '+' || 当前符号 == '-') {
            parseTerm();
            combTerm();
        }
        return expr;
    }
```

此处涉及到正负号的处理，因此不在方法中区分加减，而是新建合并的方法`Expr.combTerm(ArrayList<Ele> list, String s)`，`s`代表符号。若`s`为负号，就通过`Expr.reverseList(ArrayList<Ele> list)`取反，然后使用`addAll()`，考虑性能，我们需要化简，在`combTerm`之后使用`merge()`合并同类项。

2.`parseTerm()`用于解析项，项由乘法运算符连接若干因子组成，总体架构与上文相似，只是要重新写一个乘法的方法，也就是`Expr.mulTerm(ArrayList<Ele>)`能够实现两个`ArrayList<Ele>`的相乘（实际上就是多个相加）。

3.`parseFactor()`用于解析因子。由定义知，因子包括幂函数，常数因子和表达式因子。

```java
public parseFactor() {
        if (符号 == '(') { //表达式因子，函数表达式（已在PreStr中套上了一层括号，因此也可以当成表达式因子处理）
            parseExpr();
            if (下一个符号是'^') { //下一个一定为指数
                //读入下一个整数n;
                if (n == 0) { //考虑特殊情况
                    //置1;
                } else {
                    //重复的乘原表达式n-1次;
                }
            }
            return expr;
        } else if (符号 == '+' || 符号 == '-') { //不是第一个带符号的因子，则一定为有符号常数
            //读入常数n并结合当前符号;
            return expr;
        } else if (符号为数字) { //处理无符号整数
           // 读入常数n;
            return expr;
        } else { //处理幂函数
            return  parsePow();
        }
    }
```

4.`parsePow()`同上文分支一样，用于解析幂函数，并返回一个`Expr`类。这里单独写是因为方法行数太多影响码风：)

#### 转化为字符串并打印

这一点主要体现在`Expr`类中的`toString()`方法。总的思路是设置`StringBuilder`然后不断`append`。对`ArrayList`中的每一个`Ele`依次判断是否有'+'，是否为正负1，幂次是否为0，整体是否为0。（这一部分细节还挺多的需要特别注意）

### 基于度量的程序结构分析

#### 代码规模分析

![](https://pic.superbed.cc/item/66ab4887fcada11d37644047.png)

核心代码在450行左右，`Expr`类行数最多，占到了175行，其次是119行的解析`Parser`类。

#### 复杂度分析

##### 方法复杂度

| Method                                            | CogC | ev(G) | iv(G) | v(G) |
| :------------------------------------------------ | :--- | :---- | :---- | :--- |
| Lexer.Lexer(String)                               | 0    | 1     | 1     | 1    |
| Lexer.getNumber()                                 | 2    | 1     | 3     | 3    |
| Lexer.next()                                      | 3    | 2     | 3     | 4    |
| Lexer.read()                                      | 0    | 1     | 1     | 1    |
| MainClass.main(String[])                          | 0    | 1     | 1     | 1    |
| Parser.Parser(Lexer)                              | 0    | 1     | 1     | 1    |
| Parser.dealEx(Expr)                               | 0    | 1     | 1     | 1    |
| Parser.parseExpr()                                | 5    | 1     | 5     | 5    |
| Parser.parseFactor()                              | 16   | 4     | 9     | 10   |
| Parser.parsePow()                                 | 2    | 1     | 2     | 2    |
| Parser.parseTerm()                                | 9    | 1     | 6     | 7    |
| PreStr.pre(String)                                | 6    | 1     | 6     | 7    |
| expr.Ele.Ele()                                    | 0    | 1     | 1     | 1    |
| expr.Ele.canMerge(Ele)                            | 2    | 1     | 3     | 4    |
| expr.Ele.compareTo(Object)                        | 7    | 5     | 5     | 5    |
| expr.Ele.getCoe()                                 | 0    | 1     | 1     | 1    |
| expr.Ele.getHashVar()                             | 0    | 1     | 1     | 1    |
| expr.Ele.initHashVar(HashMap<String, BigInteger>) | 0    | 1     | 1     | 1    |
| expr.Ele.isOne()                                  | 4    | 1     | 4     | 6    |
| expr.Ele.isZero()                                 | 0    | 1     | 1     | 1    |
| expr.Ele.setCoe(BigInteger)                       | 0    | 1     | 1     | 1    |
| expr.Ele.setHashVar(String, BigInteger)           | 0    | 1     | 1     | 1    |
| expr.Ele.zero()                                   | 0    | 1     | 1     | 1    |
| expr.Expr.combTerm(ArrayList, String)             | 1    | 1     | 2     | 2    |
| expr.Expr.getExprList()                           | 0    | 1     | 1     | 1    |
| expr.Expr.initExpr(Ele)                           | 0    | 1     | 1     | 1    |
| expr.Expr.initExprList(ArrayList)                 | 0    | 1     | 1     | 1    |
| expr.Expr.merge()                                 | 10   | 4     | 7     | 7    |
| expr.Expr.mulTerm(ArrayList)                      | 3    | 1     | 3     | 3    |
| expr.Expr.reverse()                               | 1    | 1     | 2     | 2    |
| expr.Expr.reverseList(ArrayList)                  | 1    | 1     | 2     | 2    |
| expr.Expr.sort()                                  | 9    | 5     | 5     | 5    |
| expr.Expr.toString()                              | 16   | 1     | 10    | 11   |
| expr.Expr.varStr(Ele)                             | 21   | 1     | 16    | 16   |
| Total                                             | 118  | 49    | 109   | 117  |
| Average                                           | 3.47 | 1.44  | 3.21  | 3.44 |

##### 类复杂度

![](https://pic.superbed.cc/item/66ab48a1fcada11d37645283.png)

表中可以看出最复杂的三个方法：`Parser.parseFactor()`，`expr.Expr.toString()`和`expr.Expr.varStr(Ele)`。由于因子的可能类型较多，讨论情况较多，因此复杂度也较高。后两者都是转化为字符串的方法，由于情况种类较多，因此复杂度上升较显著。

图中也能看到`Expr`类，`Ele`类和`Parser`类的复杂度较高，`Parser`类中分解的方法较多，`Expr`和`Ele`中计算合并之类的方法较多导致了这几个类的复杂度较高。

###### 附：度量分析条目解释

- CogC：认知复杂度
- ev(G)：非抽象方法的基本复杂度，用以衡量一个方法的控制流结构缺陷，范围是 [1, v(G)]
- iv(G)：方法的设计复杂度，用以衡量方法控制流与其他方法之间的耦合程度，范围是 [1, v(G)]
- v(G)：非抽象方法的圈复杂度，用以衡量每个方法中不同执行路径的数量
- OC：类的非抽象方法圈复杂度，继承类不计入
- WMC：类的总圈复杂度

#### UML类图

![](https://pic.superbed.cc/item/66ab48bafcada11d37646547.jpg)

每个类的设计考虑已在上文的设计与架构中说明，此处不再赘述。

## 第二次作业总结

### 设计与架构

本次作业新增了这些要求：

- 支持嵌套多层括号
- 新增三角函数因子，三角函数括号内部包含任意因子
- 新增自定义函数因子，但自定义函数的函数表达式中不会调用其他函数

因此在第一次作业的基础上，需要完成这些工作：

1. 替换自定义函数
2. 引入新的数据结构能够存储三角因子
3. 增加或完善方法并完成三角函数的计算与化简

#### 替换自定义函数

##### `DeFun`

仅仅实现替换，替换之后即可按照之前的递归下降实现表达式解析：

![](https://pic.superbed.cc/item/66ab48d0fcada11d376472dd.png)

- 根据'='分割成左右两字符串，左边的可根据','找出形参，将形参和右边的表达式存入`HashMap`
- 在原表达式中通过`process()`替换所有的自定义函数

#### 处理三角因子

##### `Ele`

本次作业每个`Ele`类型应形如：

![](https://pic.superbed.cc/item/66ab48e7fcada11d376484ba.png)

将三角因子括号内部的因子`toString`作为`key`，指数作为`value`再存入`HashMap`：

![](https://pic.superbed.cc/item/66ab48f9fcada11d376494b9.png)

增加了处理三角因子的方法：

![](https://pic.superbed.cc/item/66ab490cfcada11d3764a49b.png)

##### `Expr`

加法和乘法只需在原有基础上添加三角因子的运算即可（加法乘法需要在`canMerge()`中添加条件)。

![](https://pic.superbed.cc/item/66ab492efcada11d3764be2f.png)

在`toString()`中加上打印三角因子的方法`triStr(Ele)`。

### 基于度量的程序结构分析

#### 代码规模分析

![](https://pic.superbed.cc/item/66ab494cfcada11d3764d335.png)

核心代码在742行左右，`Expr`类行数最多，占到了259行，其次是150行的解析`Parser`类。

#### 复杂度分析

##### 方法复杂度

| Method                                            | CogC | ev(G) | iv(G) | v(G) |
| :------------------------------------------------ | :--- | :---- | :---- | :--- |
| DeFun.max(int, int)                               | 0    | 1     | 1     | 1    |
| DeFun.process(String)                             | 22   | 6     | 13    | 13   |
| DeFun.readFun(String)                             | 6    | 3     | 5     | 6    |
| Lexer.Lexer(String)                               | 0    | 1     | 1     | 1    |
| Lexer.getNumber()                                 | 2    | 1     | 3     | 3    |
| Lexer.next()                                      | 5    | 2     | 3     | 6    |
| Lexer.read()                                      | 0    | 1     | 1     | 1    |
| MainClass.main(String[])                          | 2    | 1     | 3     | 3    |
| Parser.Parser(Lexer)                              | 0    | 1     | 1     | 1    |
| Parser.dealEx(Expr)                               | 0    | 1     | 1     | 1    |
| Parser.parseExpr()                                | 5    | 1     | 5     | 5    |
| Parser.parseFactor()                              | 18   | 5     | 11    | 12   |
| Parser.parsePow()                                 | 2    | 1     | 2     | 2    |
| Parser.parseTerm()                                | 9    | 1     | 6     | 7    |
| Parser.parseTri()                                 | 8    | 1     | 4     | 4    |
| PreStr.pre(String)                                | 6    | 1     | 6     | 7    |
| expr.Ele.Ele()                                    | 0    | 1     | 1     | 1    |
| expr.Ele.canMerge(Ele)                            | 4    | 1     | 4     | 6    |
| expr.Ele.compareTo(Object)                        | 27   | 13    | 12    | 13   |
| expr.Ele.getCoe()                                 | 0    | 1     | 1     | 1    |
| expr.Ele.getHashCos()                             | 0    | 1     | 1     | 1    |
| expr.Ele.getHashSin()                             | 0    | 1     | 1     | 1    |
| expr.Ele.getHashVar()                             | 0    | 1     | 1     | 1    |
| expr.Ele.initHashCos(HashMap<String, BigInteger>) | 0    | 1     | 1     | 1    |
| expr.Ele.initHashSin(HashMap<String, BigInteger>) | 0    | 1     | 1     | 1    |
| expr.Ele.initHashVar(HashMap<String, BigInteger>) | 0    | 1     | 1     | 1    |
| expr.Ele.isOne()                                  | 6    | 1     | 5     | 8    |
| expr.Ele.isZero()                                 | 0    | 1     | 1     | 1    |
| expr.Ele.putHashCos(HashMap<String, BigInteger>)  | 4    | 1     | 3     | 3    |
| expr.Ele.putHashSin(HashMap<String, BigInteger>)  | 4    | 1     | 3     | 3    |
| expr.Ele.setCoe(BigInteger)                       | 0    | 1     | 1     | 1    |
| expr.Ele.setHashCos(String, BigInteger)           | 0    | 1     | 1     | 1    |
| expr.Ele.setHashSin(String, BigInteger)           | 0    | 1     | 1     | 1    |
| expr.Ele.setHashVar(String, BigInteger)           | 0    | 1     | 1     | 1    |
| expr.Ele.zero()                                   | 0    | 1     | 1     | 1    |
| expr.Expr.combTerm(ArrayList, String)             | 1    | 1     | 2     | 2    |
| expr.Expr.getExprList()                           | 0    | 1     | 1     | 1    |
| expr.Expr.initExpr(Ele)                           | 0    | 1     | 1     | 1    |
| expr.Expr.initExprList(ArrayList)                 | 0    | 1     | 1     | 1    |
| expr.Expr.merge()                                 | 10   | 4     | 7     | 7    |
| expr.Expr.mulTerm(ArrayList)                      | 3    | 1     | 3     | 3    |
| expr.Expr.reverse()                               | 1    | 1     | 2     | 2    |
| expr.Expr.reverseList(ArrayList)                  | 1    | 1     | 2     | 2    |
| expr.Expr.simplify()                              | 4    | 3     | 4     | 4    |
| expr.Expr.sort()                                  | 32   | 13    | 12    | 13   |
| expr.Expr.toString()                              | 16   | 1     | 10    | 11   |
| expr.Expr.triStr(Ele)                             | 14   | 1     | 11    | 11   |
| expr.Expr.varStr(Ele)                             | 21   | 1     | 16    | 16   |
| expr.Term.getTermList()                           | 0    | 1     | 1     | 1    |
| Total                                             | 233  | 90    | 180   | 195  |
| Average                                           | 4.75 | 1.83  | 3.67  | 3.97 |

##### 类复杂度

![](https://pic.superbed.cc/item/66ab4967fcada11d3764e765.png)

#### UML类图

![](https://pic.superbed.cc/item/66ab4989fcada11d37650132.jpg)

每个类的设计考虑已在上文的设计与架构中说明，此处不再赘述。

## 第三次作业总结

### 设计与架构

本次作业新增了这些要求：

- 自定义函数定义时可以调用已经定义过的自定义函数
- 新增求导因子

因此，在第二次作业的基础上，需要完成这些工作：

1. 在`DeFun`类中完善方法使支持调用定义过的自定义函数并且包含求导因子
2. 在`Expr`类中新增求导的方法

#### 完善替换自定义函数的方法

##### `DeFun`

在同一个类中替换已经存在于HashMap中的自定义函数，然后替换求导因子（根据题目要求，需要在这里将求导因子化简并替换），因此我们直接复用之前的Parser类解析求导因子内部的表达式，并且用`toString()`替换原求导因子。

下面是可能的示例：

```java
if (str.contains("d")) { //求导因子只出现一次
            int begin = str.indexOf("d");
            //subString = dx(表达式) | dy(表达式) | dz(表达式)，是求导因子
            String subString = sim.substring(/*将dx()或dy()或dz()的子串提取出来*/);
            String derFact = subString.substring(2); //derFact是需要求导的表达式
            parser.parseExpr();//预处理，解析，返回一个Expr类
            expr.derExpr();//括号内的表达式进行求导能够返回一个Expr类
            str = str.replace(subString, "(" + expr + ")"); //将整个求导因子剔除（替换）
        }
```

#### 增加求导的方法

##### `Ele`

在`Ele`中增加方法支持判断该`Ele`是否包含该变量（'x','y','z')，若不包含，则求导的结果为零，反之则可进行正常的求导。

![](https://pic.superbed.cc/item/66ab49b0fcada11d37651f94.png)

##### `Expr`

增加支持求导的方法，`derExpr(char)`中对参数`char`作为自变量求导，分别对每一个`Ele`的变量部分，三角因子部分进行求导再合并。

![](https://pic.superbed.cc/item/66ab49c5fcada11d37652fa1.png)

### 基于度量的程序结构分析

#### 代码规模分析

![](https://pic.superbed.cc/item/66ab49dcfcada11d376541d1.png)

核心代码在899行左右，`Expr`类行数最多，占到了370行，其次是173行的解析`Expr`类。

#### 复杂度分析

##### 方法复杂度

| Method                                       | CogC | ev(G) | iv(G) | v(G) |
| :------------------------------------------- | :--- | :---- | :---- | :--- |
| DeFun.findRi(int, String)                    | 9    | 5     | 6     | 6    |
| DeFun.max(int, int)                          | 0    | 1     | 1     | 1    |
| DeFun.process(String)                        | 10   | 1     | 8     | 8    |
| DeFun.readFun(String)                        | 7    | 3     | 6     | 7    |
| Ele.Ele()                                    | 0    | 1     | 1     | 1    |
| Ele.canMerge(Ele)                            | 4    | 1     | 4     | 6    |
| Ele.compareTo(Object)                        | 27   | 13    | 12    | 13   |
| Ele.cosContain(String)                       | 3    | 3     | 2     | 3    |
| Ele.getCoe()                                 | 0    | 1     | 1     | 1    |
| Ele.getHashCos()                             | 0    | 1     | 1     | 1    |
| Ele.getHashSin()                             | 0    | 1     | 1     | 1    |
| Ele.getHashVar()                             | 0    | 1     | 1     | 1    |
| Ele.initHashCos(HashMap<String, BigInteger>) | 0    | 1     | 1     | 1    |
| Ele.initHashSin(HashMap<String, BigInteger>) | 0    | 1     | 1     | 1    |
| Ele.initHashVar(HashMap<String, BigInteger>) | 0    | 1     | 1     | 1    |
| Ele.isOne()                                  | 6    | 1     | 5     | 8    |
| Ele.isZero()                                 | 0    | 1     | 1     | 1    |
| Ele.putHashCos(HashMap<String, BigInteger>)  | 4    | 1     | 3     | 3    |
| Ele.putHashSin(HashMap<String, BigInteger>)  | 4    | 1     | 3     | 3    |
| Ele.setCoe(BigInteger)                       | 0    | 1     | 1     | 1    |
| Ele.setHashCos(String, BigInteger)           | 0    | 1     | 1     | 1    |
| Ele.setHashSin(String, BigInteger)           | 0    | 1     | 1     | 1    |
| Ele.setHashVar(String, BigInteger)           | 0    | 1     | 1     | 1    |
| Ele.sinContain(String)                       | 3    | 3     | 2     | 3    |
| Ele.varContain(String)                       | 1    | 1     | 1     | 2    |
| Ele.zero()                                   | 0    | 1     | 1     | 1    |
| Expr.combTerm(ArrayList, String)             | 1    | 1     | 2     | 2    |
| Expr.derCos(int, String)                     | 7    | 1     | 5     | 5    |
| Expr.derExpr(char)                           | 14   | 1     | 8     | 8    |
| Expr.derSin(int, String)                     | 7    | 1     | 5     | 5    |
| Expr.derVar(int, String)                     | 0    | 1     | 1     | 1    |
| Expr.getExprList()                           | 0    | 1     | 1     | 1    |
| Expr.initExpr(Ele)                           | 0    | 1     | 1     | 1    |
| Expr.initExprList(ArrayList)                 | 0    | 1     | 1     | 1    |
| Expr.merge()                                 | 10   | 4     | 7     | 7    |
| Expr.mulTerm(ArrayList)                      | 3    | 1     | 3     | 3    |
| Expr.reverse()                               | 1    | 1     | 2     | 2    |
| Expr.reverseList(ArrayList)                  | 1    | 1     | 2     | 2    |
| Expr.simplify()                              | 4    | 3     | 4     | 4    |
| Expr.sort()                                  | 32   | 13    | 12    | 13   |
| Expr.toString()                              | 16   | 1     | 10    | 11   |
| Expr.triStr(Ele)                             | 22   | 1     | 13    | 13   |
| Expr.varStr(Ele)                             | 21   | 1     | 16    | 16   |
| Lexer.Lexer(String)                          | 0    | 1     | 1     | 1    |
| Lexer.getNumber()                            | 2    | 1     | 3     | 3    |
| Lexer.next()                                 | 6    | 2     | 6     | 7    |
| Lexer.read()                                 | 0    | 1     | 1     | 1    |
| MainClass.main(String[])                     | 2    | 1     | 3     | 3    |
| Parser.Parser(Lexer)                         | 0    | 1     | 1     | 1    |
| Parser.dealEx(Expr)                          | 0    | 1     | 1     | 1    |
| Parser.parseDer(char)                        | 0    | 1     | 1     | 1    |
| Parser.parseExpr()                           | 5    | 1     | 5     | 5    |
| Parser.parseFactor()                         | 20   | 6     | 14    | 15   |
| Parser.parsePow()                            | 2    | 1     | 2     | 2    |
| Parser.parseTerm()                           | 9    | 1     | 6     | 7    |
| Parser.parseTri()                            | 8    | 1     | 4     | 4    |
| PreStr.pre(String)                           | 6    | 1     | 6     | 7    |
| Total                                        | 277  | 102   | 214   | 230  |
| Average                                      | 4.85 | 1.79  | 3.75  | 4.04 |

##### 类复杂度

![](https://pic.superbed.cc/item/66ab49f4fcada11d3765538f.png)

图中也能看到`Expr`类，`Ele`类和`Parser`类的复杂度较高，Expr类中大量的化简、计算和求导方法使得该类复杂度很高。

#### UML类图

![](https://pic.superbed.cc/item/66ab4a0bfcada11d37656557.jpg)

每个类的设计考虑已在上文的设计与架构中说明，此处不再赘述。

## bug分析

### 分析自己程序的bug

##### 第一次作业

- 一些特殊点的情况，例如：0^0+5^0+2*10^0，需要提前做好调试并在`toString()`中修改

##### 第二次作业

- 替换自定义函数时根据逗号分割，因此在面对嵌套函数例如f(g(y,z),h(x,y),sin(x))中存在多于2个逗号的情况可能会出错，解决方案是每次循环找到最靠后的一个自定义函数（保证这个函数里异地没有嵌套的自定义）进行替换
- 注意替换时索引的位置，例如f((((x,y,z))))可能会出现括号判断错误
- 注意特殊和边界点，例如sin((0))^2,sin((0))^0,cos((0))^3
- 幂次为0时应该直接读取整数而不是往后解析表达式（大概是脑子抽了才这样写，大部分类似的问题全都是这点引起的）

##### 第三次作业

- 求导之后化简会出问题，例如dx(x*cos(x)**0)最后解析得到的Expr没有问题但是输出会出错，原因在于我判断cos部分含不含x是根据HashMap是否为空（第二次作业的遗留），但是在求导之后我没有进行相应的remove工作，导致输出错误

### 分析他人程序的bug

手动构造具有代表性的边界数据，拥有多层嵌套括号，同时包含特殊条件（结果为0，1，求导内部无自变量），例如：

```javascript
1
f(x,y)=(((sin(cos(x))-(+1-sin(cos(y))))))
f((sin(x)),(y**2-0**0))+cos(0)**0-sin(0)**0
0
dx(x*cos(x)**0)
```

## 心得体会

第一单元作业主要是针对表达式括号展开和求导的三次迭代开发。从最开始的不理解git的作用，到现在渐渐认识到版本管理在工程中的重要性；从对IDEA的无所适从和屡次犯错，到逐渐领略到这款IDE功能的强大；从最开始的对Java感到陌生，到逐渐熟悉它的众多优良简便的方法、数据结构和其面向对象的思维。同时，三次作业对代码风格的要求使我进一步意识到码风的重要性并逐渐内在地提升了自己编程的码风。第一次作业由于只有整体的架构，在缺少对Java语法以及数据结构等知识的缺乏的情况下，完成得非常艰难。在第二次第三次的迭代开发中，任务量明显比第一次的从零开始要轻松一些，但是要特别特别注意类以及方法之间的耦合性，尽量不改变现有的方法，这样更容易发现代码的bug。最后，我认为可以多参考讨论区里同学们的设计思路，这对于我的架构设计有很大的帮助^_^ 感谢助教和同学们的热心帮助！
