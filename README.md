# uppdatera-agent
Purposely 💉 code changes in class files


## Example of manipulating the control-flow of dependencies: Twitter text Parser

### Setup

Let's add this as a dependency in the `pom.xml file`

```xml
    <dependencies>
        <dependency>
            <groupId>com.twitter.twittertext</groupId>
            <artifactId>twitter-text</artifactId>
            <version>3.0.1</version>
        </dependency>
    </dependencies>
 ```   

Here is the example code:

```java
import com.twitter.twittertext.TwitterTextParser;

public class TestExample{

    public static void main(String[] args)  {
       var res = TwitterTextParser.parseTweet("This is a completely valid Tweet!");

       if(res.isValid == true){
           System.out.println("You failed to manipulate Joseph!");
       } else {
           System.out.println("Bytecode manipulation ftw!");
       }
    }

}
```

The text is valid a valid tweet and should return `true` 

### We want to manipulate such that the `isValid` condition incorrectly returns false

``` java
   @Nonnull
    private static TwitterTextParseResults parseTweet(@Nullable String tweet, @Nonnull TwitterTextConfiguration config, boolean extractURLs) {
        if (tweet != null && tweet.trim().length() != 0) {
            String normalizedTweet = Normalizer.normalize(tweet, Form.NFC);
            int tweetLength = normalizedTweet.length();
            if (tweetLength == 0) {
                return EMPTY_TWITTER_TEXT_PARSE_RESULTS;
            } else {
            ...
```
We want to manipulate the variable  `tweetLength` to contain `0` before executing `if (tweetLength == 0)`.

The bytecode for this snippet:

```
 aload 0
    getstatic 'java/text/Normalizer$Form.NFC','Ljava/text/Normalizer$Form;'
    INVOKESTATIC java/text/Normalizer.normalize (Ljava/lang/CharSequence;Ljava/text/Normalizer$Form;)Ljava/lang/String;
    astore 3
    aload 3
    INVOKEVIRTUAL java/lang/String.length ()I
    istore 4
    iload 4
    ifne l2
    getstatic 'com/twitter/twittertext/TwitterTextParser.EMPTY_TWITTER_TEXT_PARSE_RESULTS','Lcom/twitter/twittertext/TwitterTextParseResults;'
    areturn
```

Before executing `iload 4` and then `ifne l2`, we want to store a different value at index 4. We inject these instructions:

```
iconst_0
istore 4
```

This puts 0 on the stack and the pop to the array index 4 (variable `tweetLength`)

We are done!

## Running it with the agent
```jshelllanguage
 java -javaagent:target/uppdatera-agent-1.0-SNAPSHOT-jar-with-dependencies.jar com.github.jhejderup.TestExample
```

## Let's see the results

```
Starting the agent
com/twitter/twittertext/TwitterTextParser
Visiting method: parseTweet
Visiting method: parseTweet
Visiting method: parseTweet
Bytecode manipulation ftw!
```
## Now let's generalize it!

We can inject two types of changes

 - Control flow changes
 - Data flow changes

The goal is to enforce the program to perform a different execution path by manipulating the logic

![IMAGE 2019-06-28 14:32:28](https://user-images.githubusercontent.com/2521475/60342367-946ddd80-99b1-11e9-937b-02c063925a5a.jpg)

As input, we take a class and a method. The method must belong to the class.

1. For CFC, we manipulate (actually flip) branch conditions 
2. For DFC, we manipulate primitive types (e.g., Integer or String) to reach new dataflow decions

All of these serves as a cheap "proxy" for introducing unexpected behaviour (due to code changes)

**Q1**: What about try-catch statements? tricky to implement in bytecode

### Which methods should we change?

We can avoid some randomness by actually taking methods with high churn in past hisotry (e.g., hot spots). These methods are likely to change later and it is important to check whether Uppdatera and test suites can cover those



 

