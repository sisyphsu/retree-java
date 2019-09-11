package com.github.sisyphsu.retree;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Normal regular expression test, make sure retree works fine for all case.
 *
 * @author sulin
 * @since 2019-08-23 14:24:58
 */
public class RagularTest {

    @Test
    public void testBasic() {
        assert !matches("in", "Linux");
        assert find("in", "Linux");

        assert matches(".", "a"); //true
        assert matches(".", " "); //true
        assert matches(".z", "cz"); //true
        assert !matches(".z", "cb"); //false
        assert matches(".z", "9z"); //true
        assert matches("..e", "the"); //true
        assert matches("t.e", "the"); //true
        assert matches("...", "the"); //true
        assert !matches("i.u", "Linux");
        assert find("i.u", "Linux"); //true

        assert matches("[0-9]", "9"); //true
        assert matches("[a-z][0-9]", "t5"); //true
        assert matches("[jJ][aA][vV][aA]", "jAva"); //true
        assert matches(".[aA][vV][aA]", "mAva"); //true
        assert !matches("[jJ][aA].[aA]", "lAva"); //false
        assert matches("[A-Z][a-z].java", "My.java"); //true
        assert matches("[A-Z][a-z].java", "My8java"); //true
        assert !matches("[A-Z][a-z]\\.java", "My8java"); //false
        assert matches("[A-Z][a-z]\\.java", "My.java"); //true
        assert !matches("[A-Z][a-z][.]java", "My8java"); //false
        assert find("[A-Z]", "USA");

        assert matches("[1-9][1-9]-[1-9][1-9]", "38-99"); //true

        assert matches("\\d", "4"); //true
        assert !matches("\\d", "c"); //false
        assert matches("\\D", "a"); //true
        assert !matches("\\W", "c"); //false
        /* In following two examples both are false because white-space is not an alphabet nor a digit*/
        assert !matches("\\w", " "); //false
        assert !matches("\\d", " "); //false
        assert matches("\\s", " "); //true
        assert matches("\\D", " "); //true
        assert matches("\\W", " "); //true
        assert find("\\w", "cd"); //false

        assert matches("\\w\\w", "cd"); //true
        assert matches("[\\D]\\d", "b4"); //true
        assert matches("[a-z]\\s[0-9]", "a 4"); //true
        assert !matches("\\D\\d\\w\\W", "w9s4"); //false
        assert matches("\\w\\W", "_@"); //true
        assert matches("\\W", "."); //true
        assert matches("\\.", "."); //true

        assert find("^The", "The line");//matches:  'The' at 0-3 'The line'
        assert !matches("^The", "This is The line");//no matches
        assert find("line$", "The line");//matches:  'line' at 4-8 'The line'
        assert find("\\bline", "The line");//matches:  'line' at 4-8 'The line'
        assert find("is", "This is island");//matches:  'is' at 2-4, 'is' at 5-7, 'is' at 8-10 'This is island'
        assert find("\\bis", "This is island");//matches:  'is' at 5-7, 'is' at 8-10 'This is island'
        assert find("\\bis\\b", "This is island");//matches:  'is' at 5-7 'This is island'
        assert find("line", "The inclined line");//matches:  'line' at 7-11, 'line' at 13-17 'The inclined line'
        assert find("\\bline", "The inclined line");//matches:  'line' at 13-17 'The inclined line'
        assert find("line\\b", "The inclined line");//matches:  'line' at 13-17 'The inclined line'
        assert find("lined\\b", "The inclined line");//matches:  'lined' at 7-12 'The inclined line'
        assert find("\\bi", "water is inside inland");//matches:  'i' at 6-7, 'i' at 9-10, 'i' at 16-17 'water is inside inland'
        assert find("\\bin", "water is inside inland");//matches:  'in' at 9-11, 'in' at 16-18 'water is inside inland'

        assert matches("a|b", "a"); //true
        assert find("a|b", "alphabet");//result: 'XlphXXet'
        assert matches("[a-d]|[x-z]", "x"); //true
        assert find("[a-d][x-z]|[^*&%]", "cy%"); // match 'cy'

        /* This will still match. Engine will match either everything to the left or to the right of the pipe*/
        assert find("Gravity|levity", "levity Gravity Gravitlevity");//matches:  'levity' at 0-6, 'Gravity' at 7-14, 'levity' at 21-27 'levity Gravity Gravitlevity'
        assert matches("Lauretta Demaria|Jannette Ballard", "Jannette Ballard");//matches:  'Jannette Ballard' at 0-16 'Jannette Ballard'
        assert matches("This is Lauretta Demaria|Jannette Ballard", "This is Lauretta Demaria");//matches:  'This is Lauretta Demaria' at 0-24 'This is Lauretta Demaria'

        /* The complete sentence doesn't match because of the same reason mentioned above. We have to use groups (next section) for this kind of situations.*/
        assert find("This is Lauretta Demaria|Jannette Ballard", "This is Jannette Ballard");//matches:  'Jannette Ballard' at 8-24 'This is Jannette Ballard'
        assert matches("This is (Lauretta Demaria|Jannette Ballard)", "This is Jannette Ballard");//matches:  'This is Jannette Ballard' at 0-24 'This is Jannette Ballard'
        assert find("l(og|yr)ic", "logic lyric loyric");//matches:  'logic' at 0-5, 'lyric' at 6-11 'logic lyric loyric'
        assert find("l(og|yr)ic", "logic lyric loric");//matches:  'logic' at 0-5, 'lyric' at 6-11 'logic lyric loric'

        /* Following example shows the 12 hour time regex*/
        assert matches("([1-9]|1[012]):[0-5][0-9]", "3:59"); //true
        assert !matches("([1-9]|1[012]):[0-5][0-9]", "3:70"); //false
    }

    @Test
    public void testGreedy() {
        /* This quantifier doesn't match zero-length input*/
        assert !matches("a+", ""); //false
        assert !matches(".+", ""); //false
        assert find("a+", "ababaaaab");//matches:  'a' at 0-1, 'a' at 2-3, 'aaaa' at 4-8 'ababaaaab'

        /* The first 'b' in the input string doesn't match because there has to be at least one 'a' before 'b', that's the definition of '+' quantifier*/
        assert !matches("a+b", "bbbaaab");
        assert find("a+b", "bbbaaab");//matches:  'aaab' at 3-7 'bbbaaab'

        /* The first 'ab' matches. Compare the last example.*/
        assert find("a+b", "abbbaaab");//matches:  'ab' at 0-2, 'aaab' at 4-8 'abbbaaab'

        /* In this example the regex part 'a.*' eats the entire input 'axyb', and then looking at last literal part 'b' (nothing left for a match) it backtracks and give up the last eaten character b. Please see the left column for more examples.*/
        assert matches("a.+b", "axyb");//matches:  'axyb' at 0-4 'axyb'

        /* In this example the entire input string is not satisfying 'a[.\\S]' because this part is expecting 'a' at the left followed by any characters but not white-space. In this case, the input string will be divided into two parts 'aaaxb' and 'azzzeb' as both are satisfying 'a[.\\S]'. Both will be eaten entirely by 'a[.\\S]' then backtracking will happen in both to match remaining part 'b' of the expression.*/
        assert find("a[.\\S]+b", "aaaxb azzzeb");//matches:  'aaaxb' at 0-5, 'azzzeb' at 6-12 'aaaxb azzzeb'
        assert matches(".+x", "abc x");//matches:  'abc x' at 0-5 'abc x'
        assert matches("a+bx", "aaabx");//matches:  'aaabx' at 0-5 'aaabx'
        assert find("x+", "xxbxxx");//matches:  'xx' at 0-2, 'xxx' at 3-6 'xxbxxx'

        /* Note there's no greedy eating here too. The pattern must start */
        assert matches("A.+", "AEG");//matches:  'AEG' at 0-3 'AEG'

        /* Matches xyz as one entity, one ore more times*/
        assert find("(xyz)+", "zz xyz abc xyzxyz xy");//matches:  'xyz' at 3-6, 'xyzxyz' at 11-17 'zz xyz abc xyzxyz xy'
        assert find("(xyz)+", "zz xyz abc xyzxyz xy");//result: 'zz S abc S xy'

        /* Either a or b, one or more times*/
        assert find("(a|b)+", "aaaa bb cabc");//matches:  'aaaa' at 0-4, 'bb' at 5-7, 'ab' at 9-11 'aaaa bb cabc'

        /* 'abcxyz' and 'xyzabc' are expectedly matched but why matched together instead of separately? That's because it's the greedy quantifier, the regex engine tries to match as many characters it can in one attempt.*/
        assert find("((abc)|(xyz))+", "abc xyz zyxabc abcxyz xyzabc");//matches:  'abc' at 0-3, 'xyz' at 4-7, 'abc' at 11-14,
        //'abcxyz' at 15-21, 'xyzabc' at 22-28
        //'abc xyz zyxabc abcxyz xyzabc'

        assert find("[0-9]+", "9823 340");//matches:  '9823' at 0-4, '340' at 5-8 '9823 340'
        assert matches("([0-9]|\\W)+", "9823 340");//matches:  '9823 340' at 0-8 '9823 340'

        /* Greedy quantifier picks the longest match.*/
        assert matches(".+ten", "AtenAAAAAten");//matches:  'AtenAAAAAten' at 0-12 'AtenAAAAAten'
        assert find("A.+ten", "xtenAAAAAten");//matches:  'AAAAAten' at 4-12 'xtenAAAAAten'

        assert find("ten+", "tenAAAAAten");//matches:  'ten' at 0-3, 'ten' at 8-11 'tenAAAAAten'

        /* Matching a file extension*/
        assert find("(\\.[^.]+)$", "abc.xyz.appp.version.exe");//matches:  '.exe' at 20-24 'abc.xyz.appp.version.exe'

        /* Matches empty input because this quantifier allows zero-length match.*/
        assert matches(".*", ""); //true
        assert matches(".*", "");//matches:  '' at 0-0
        assert matches(".*", "Anything matches"); //true

        /* Notice zero-length match at the end.*/
        assert matches(".*", "Anything matches");//matches:  'Anything matches' at 0-16, '' at 16-16 'Anything matches'
        assert matches(".*", "Anything matches");//result: 'S TS T'
        assert matches(".*x", "Sandbox"); //true

        /* Notice no zero-length result here.*/
        assert matches(".*x", "Sandbox");//matches:  'Sandbox' at 0-7 'Sandbox'
        assert matches("x.*", "xenon");//matches:  'xenon' at 0-5 'xenon'
        assert matches(".*test", "testyyxxtest"); //true

        /* Greedy behavior*/
        assert matches(".*test", "testyyxxtest");//matches:  'testyyxxtest' at 0-12 'testyyxxtest'

        /* The first white space is the match for '\\W' part. Also notice there's no zero-length match at the end, because '\\W.*' doesn't match empty or zero-length string.*/
        assert find("\\W.*", "this is a sentence");//matches:  ' is a sentence' at 4-18 'this is a sentence'

        /* Notice zero-length matches with each attempt. The pattern 't*' can accept empty string*/
        assert find("t*", "yx");//matches:  '' at 0-0, '' at 1-1, '' at 2-2

        /* The reason we don't have zero-length match for each y sitting at the end of the input string is that: the engine can see '*a' as empty but expects exact 'bc' given by the whole pattern '*abc'*/
        assert find(".*abc", "zzzzabcyyy");//matches:  'zzzzabc' at 0-7 'zzzzabcyyy'
        assert find("s*", "ssssssst");//matches:  'sssssss' at 0-7, '' at 7-7, '' at 8-8 'ssssssst'

        /* In this replaceAll example the 'XY ' is placed exactly at the positions given by the matched indexes shown in the last example. In the result, there's also a 't' at the end, because t is not a match and cannot be replaced.*/
        assert find("l.*c", "appeal to logic");//matches:  'l to logic' at 5-15 'appeal to logic'
        assert find("\\bl.*c\\b", "appeal to logic");//matches:  'logic' at 10-15 'appeal to logic'

        /* Matching Even numbers*/
        assert find("\\d*[13579]", "3 46 33 88 133");//matches:  '3' at 0-1, '33' at 5-7, '133' at 11-14 '3 46 33 88 133'

        /* Matching HTML tag without attributes*/
        assert matches("<[A-Za-z][A-Za-z0-9]*>", "<span>");//matches:  '<span>' at 0-6 '<span>'

        /* It allows zero-length matches*/
        assert matches(".?", "");//matches:  '' at 0-0

        /* It doesn't allow more than one match. That means it will not match 'aaaa' together*/
        assert find(".?", "aaaa");//matches:  'a' at 0-1, 'a' at 1-2, 'a' at 2-3, 'a' at 3-4, '' at 4-4 'aaaa'

        /* Notice zero length match for b and at the end*/
        assert find("a?", "aab");//matches:  'a' at 0-1, 'a' at 1-2, '' at 2-2, '' at 3-3 'aab'
        assert find("a?", "aab");//result: 'X-X-X-bX-'
        assert find(".?", "red car");//matches:  'r' at 0-1, 'e' at 1-2, 'd' at 2-3, ' ' at 3-4, 'c' at 4-5, 'a' at 5-6, 'r' at 6-7, '' at 7-7 'red car'
        assert find("(red)?", "red car");//matches:  'red' at 0-3, '' at 3-3, '' at 4-4, '' at 5-5, '' at 6-6, '' at 7-7 'red car'
        assert find("([0-9][0-9][A-Z])?", "89C34D555");//matches:  '89C' at 0-3, '34D' at 3-6, '' at 6-6, '' at 7-7, '' at 8-8, '' at 9-9 '89C34D555'
        assert find("wall(paper)?", "wall wallpaper paper");//matches:  'wall' at 0-4, 'wallpaper' at 5-14 'wall wallpaper paper'
        assert find("x{2}", "xx xxx");//matches:  'xx' at 0-2, 'xx' at 3-5 'xx xxx'
        assert find("x{2,}", "xx xxx xxxx");//matches:  'xx' at 0-2, 'xxx' at 3-6, 'xxxx' at 7-11 'xx xxx xxxx'
        assert find("x{1,3}", "xx xxx xxxx");//matches:  'xx' at 0-2, 'xxx' at 3-6, 'xxx' at 7-10, 'x' at 10-11 'xx xxx xxxx'

        /* This allows zero-length match*/
        assert find("x{0,2}", "xx xxx xxxx");//matches:  'xx' at 0-2, '' at 2-2, 'xx' at 3-5, 'x' at 5-6, '' at 6-6, 'xx' at 7-9, 'xx' at 9-11, '' at 11-11 'xx xxx xxxx'
        assert find("(li){2,}", "lilies milliliter");//matches:  'lili' at 0-4, 'lili' at 10-14 'lilies milliliter'

        /* Words with three letters*/
        assert find("\\b[a-z]{3,3}\\b", "A fly has big eyes");//matches:  'fly' at 2-5, 'has' at 6-9, 'big' at 10-13 'A fly has big eyes'
        assert find("[a-z][0-9]{2,3}", "aa29 a3333 3d43");//matches:  'a29' at 1-4, 'a333' at 5-9, 'd43' at 12-15 'aa29 a3333 3d43'
        assert find("([a-z][0-9]){2,2}", "aa29 a3d4a3");//matches:  'a3d4' at 5-9 'aa29 a3d4a3'

        /* This matches one of the Math methods of length 3. */
        assert find("Math[.](\\w){3,3}[(][^)(]{1,}[)];", "Math.sin(20);  Math.abs(4);");//matches:  'Math.sin(20);' at 0-13, 'Math.abs(4);' at 15-27 'Math.sin(20);  Math.abs(4);'
    }

    @Test
    public void testGroup() {
        /* Find all words ending with comma. (Not using lookahead yet.)*/
        assert find("[a-z]+,", "bat, cat, dog, fox"); //matches:  'bat,' at 0-4, 'cat,' at 5-9, 'dog,' at 10-14
    }

    @Test
    public void testRelactent() {
        assert !matches(".+?", "abc");
        assert find(".+?", "abc");

        assert find("a+?", "babaab");//matches:  'a' at 1-2, 'a' at 3-4, 'a' at 4-5 'babaab'
        assert find("a+?", "babaab");

        /* Comparing with Greedy +*/
        assert find("a+", "babaab");//matches:  'a' at 1-2, 'aa' at 3-5 'babaab'
        assert find("(xyz)+?", "xyzxyz");//matches:  'xyz' at 0-3, 'xyz' at 3-6 'xyzxyz'
        assert find("((abc)|(xyz))+?", "abcxyz xyzabc");//matches:  'abc' at 0-3, 'xyz' at 3-6, 'xyz' at 7-10, 'abc' at 10-13 'abcxyz xyzabc'

        /* Comparing with Greedy +*/
        assert find("((abc)|(xyz))+", "abcxyz xyzabc");//matches:  'abcxyz' at 0-6, 'xyzabc' at 7-13 'abcxyz xyzabc'
        assert find("[0-9]+?", "9823 340");//matches:  '9' at 0-1, '8' at 1-2, '2' at 2-3, '3' at 3-4, '3' at 5-6, '4' at 6-7, '0' at 7-8 '9823 340'

        /* Comparing with Greedy +*/
        assert find("[0-9]+", "9823 340");//matches:  '9823' at 0-4, '340' at 5-8 '9823 340'
        assert find("d.+?o", "doodododo");//matches:  'doo' at 0-3, 'dodo' at 3-7 'doodododo'
        assert matches(".*?", ""); //true
        assert matches(".*?", "");//matches:  '' at 0-0

        /* As Reluctant Quantifier find the shortest matches and for '*' shortest is zero, the next two examples gives all zero-length matches*/
        assert find(".*?", "maths");//matches:  '' at 0-0, '' at 1-1, '' at 2-2, '' at 3-3, '' at 4-4, '' at 5-5
        assert find("x*?", "xxx");//matches:  '' at 0-0, '' at 1-1, '' at 2-2, '' at 3-3
        assert find("x.*?", "xxx");//matches:  'x' at 0-1, 'x' at 1-2, 'x' at 2-3 'xxx'

        /* There's no difference between the following exmaple and the last example, as portion '.*?' in last example is interpreted as zero-lenght match */
        assert find("x", "xxx");//matches:  'x' at 0-1, 'x' at 1-2, 'x' at 2-3 'xxx'
        assert find("x.*?e", "xylose xylene xe ex");//matches:  'xylose' at 0-6, 'xyle' at 7-11, 'xe' at 14-16 'xylose xylene xe ex'

        /* Here we replace * with + which doesn't allow zero-length*/
        assert find("x.+?e", "xylose xylene xe ex");//matches:  'xylose' at 0-6, 'xyle' at 7-11, 'xe e' at 14-18 'xylose xylene xe ex'

        /* Making it greedy*/
        assert find("x.+e", "xylose xylene xe ex");//matches:  'xylose xylene xe e' at 0-18 'xylose xylene xe ex'
        assert find("\\b\\w*?bb\\w*?\\b", "bobby blub jibb lobby abba bb");//matches:  'bobby' at 0-5, 'jibb' at 11-15, 'lobby' at 16-21,
        //'abba' at 22-26, 'bb' at 27-29
        //'bobby blub jibb lobby abba bb'

        /* Matching all html tags*/
        assert find("<(.|\n)*?>", "<html><body>the page</body></html>");//matches:  '<html>' at 0-6, '<body>' at 6-12, '</body>' at 20-27, '</html>' at 27-34

        /* Making it greedy, see the difference*/
        assert find("<(.|\n)*>", "<html><body>the page</body></html>");//matches:  '<html><body>the page</body></html>' at 0-34

        assert matches(".??", "");//matches:  '' at 0-0
        assert find("x.??", "xxx");//matches:  'x' at 0-1, 'x' at 1-2, 'x' at 2-3 'xxx'

        /* silo is not a match cause the quantifier doesn't allow length more than one.*/
        assert find("s.??o", "so solo silo");//matches:  'so' at 0-2, 'so' at 3-5 'so solo silo'

        /* Replacing ? with + still keeping it reluctant*/
        assert find("s.+?o", "so solo silo");//matches:  'so so' at 0-5, 'silo' at 8-12 'so solo silo'

        /* This will also match 'My electric guitar'*/
        assert find("My\\s+(electric)??\\s*guitar", "My guitar");//matches:  'My guitar' at 0-9 'My guitar'

        /* As it's reluctant, it only matches smallest part, so 'de' is not a match*/
        assert find(".{3,}?", "abcde");//matches:  'abc' at 0-3 'abcde'

        /* Making it Greedy*/
        assert matches(".{3,}", "abcde");//matches:  'abcde' at 0-5 'abcde'
        assert find(".{4,}?", "abcdefghij");//matches:  'abcd' at 0-4, 'efgh' at 4-8 'abcdefghij'
        assert matches("(\\w{3,}?\\.){2}?\\w{3,}?", "www.example.com");//matches:  'www.example.com' at 0-15 'www.example.com'

        /* This matches sentences that contain between one to ten words.*/
        assert find("[A-Z](\\w*?\\s*?){1,10}?[.?!]", "This is a paragraph. That's it.");//matches:  'This is a paragraph.' at 0-20 'This is a paragraph. That's it.'
    }

    @Test
    public void testPossessive() {
        assert matches(".++", "abc");//matches:  'abc' at 0-3 'abc'

        /* No match because there's no backing off after reading/eating the entire input against '.++'*/
        assert !matches(".++x", "abcx");//no matches

        /* Making it greedy*/
        assert matches(".+x", "abcx");//matches:  'abcx' at 0-4 'abcx'

        /* It is just like greedy 'x+'. The longest matches are found in this case, which is different than the reluctant x+?*/
        assert find("x++", "xxbxxx");//matches:  'xx' at 0-2, 'xxx' at 3-6 'xxbxxx'

        /* Making it reluctant*/
        assert find("x+?", "xxbxxx");//matches:  'x' at 0-1, 'x' at 1-2, 'x' at 3-4, 'x' at 4-5, 'x' at 5-6 'xxbxxx'
        assert matches("A.++", "AEG");//matches:  'AEG' at 0-3 'AEG'
        assert find("((cat)|(dog))++", "cat dog catdog dogcat");//matches:  'cat' at 0-3, 'dog' at 4-7, 'catdog' at 8-14, 'dogcat' at 15-21

        /* Making it reluctant*/
        assert find("((cat)|(dog))+?", "cat dog catdog dogcat");//matches:  'cat' at 0-3, 'dog' at 4-7, 'cat' at 8-11, 'dog' at 11-14, 'dog' at 15-18, 'cat' at 18-21

        assert matches(".*+", "");//matches:  '' at 0-0

        /* No backing off that's why no match*/
        assert !matches(".*+x", "abcx");//no matches

        /* This is same as greedy x**/
        assert find("x*+", "xxbxx");//matches:  'xx' at 0-2, '' at 2-2, 'xx' at 3-5, '' at 5-5 'xxbxx'

        /* Making it reluctant. No matches because the smallest match of 'xx' is empty string.*/
        assert find("x*?", "xxbxx");//matches:  '' at 0-0, '' at 1-1, '' at 2-2, '' at 3-3, '' at 4-4, '' at 5-5

        /* No matches becuse, the part '.*+' will eat the entire input and there's no backing off.*/
        assert !matches("a.*+z", "abztstz");//no matches
        assert find("a*+z", "abztstaz");//matches:  'z' at 2-3, 'az' at 6-8 'abztstaz'
        assert !matches("a*+z", "aaaaaaa");//no matches

        /* The part 'z?' can match zero number of times*/
        assert matches("a*+z?", "aaaaaaa");//matches:  'aaaaaaa' at 0-7, '' at 7-7 'aaaaaaa'

        /* This pattern matches the single quoted strings. We can use greedy quantifier by removing '+', which will do the same thing. But making it possessive we are telling engine not to backoff and fail fast. Pattern of this kind, after all, won't accept any single quote inside the string, notice [^']. Also the primary purpose of the possessive quantifiers to get some performance benefits*/
        assert matches("'[^']*+'", "'Reason and Rationality'");//matches:  ''Reason and Rationality'' at 0-24 ''Reason and Rationality''
        assert matches(".?+", "");//matches:  '' at 0-0
        assert find(".?+", "abc");//matches:  'a' at 0-1, 'b' at 1-2, 'c' at 2-3, '' at 3-3 'abc'
        assert find(".?+x", "yyyyx");//matches:  'yx' at 3-5 'yyyyx'

        /* x doesn't match because '.?+' will eat the entire string which is only 'x' here and there's no backing off for possessive '+'*/
        assert !matches(".?+x", "x");//no matches
        assert find("x?+", "xyz");//matches:  'x' at 0-1, '' at 1-1, '' at 2-2, '' at 3-3 'xyz'
        assert find("x?+z", "xyzyzzxyxzy");//matches:  'z' at 2-3, 'z' at 4-5, 'z' at 5-6, 'xz' at 8-10 'xyzyzzxyxzy'
        assert matches(".{3,}+", "abcd");//matches:  'abcd' at 0-4 'abcd'
        assert !matches("a.{0,}+z", "abcdz");//no matches
        assert !matches(".*+abc", "aabc");//no matches
    }

    @Test
    public void testCapturingGroup() {
        String regex = "\\b([A-Za-z\\s]+),\\s([A-Z]{2,2}):\\s([0-9]{3,3})\\b";
        String input = "This is the list: Baytown, TX: 281, Chapel Hill, NC: 284, Fort Myers, FL: 239";

        ReMatcher matcher = new ReMatcher(new ReTree(regex), input);

        int matchCount = 0;
        List<String> list = new ArrayList<>();
        while (matcher.find()) {
            matchCount++;
            System.out.printf("Match count: %s, Group Zero Text: '%s'%n", matchCount, matcher.group(0));
            for (int i = 1; i <= matcher.groupCount(); i++) {
                CharSequence str = matcher.group(i);
                System.out.printf("Capture Group Number: %s, Captured Text: '%s'%n", i, str);
                list.add(str.toString());
            }
        }
        assert list.size() == 9;
        assert list.get(0).equals("Baytown");
        assert list.get(1).equals("TX");
        assert list.get(2).equals("281");
        assert list.get(3).equals("Chapel Hill");
        assert list.get(4).equals("NC");
        assert list.get(5).equals("284");
        assert list.get(6).equals("Fort Myers");
        assert list.get(7).equals("FL");
        assert list.get(8).equals("239");
    }

    @Test
    public void testNamedCapturingGroup() {
        String input = "This is the list: Baytown, TX: 281, Chapel Hill, NC: 284, Fort Myers, FL: 239";
        String regex = "\\b(?<city>[A-Za-z\\s]+),\\s(?<state>[A-Z]{2,2}):\\s(?<areaCode>[0-9]{3,3})\\b";


        ReMatcher matcher = new ReMatcher(new ReTree(regex), input);

        List<Map<String, String>> list = new ArrayList<>();

        while (matcher.find()) {
            if (matcher.groupCount() == 3) {
                Map<String, String> map = new HashMap<>();
                map.put("city", matcher.group("city").toString());
                map.put("state", matcher.group("state").toString());
                map.put("areaCode", matcher.group("areaCode").toString());
                list.add(map);
            }
        }
        assert list.size() == 3;
        assert list.get(0).get("city").equals("Baytown");
        assert list.get(0).get("state").equals("TX");
        assert list.get(0).get("areaCode").equals("281");
        assert list.get(1).get("city").equals("Chapel Hill");
        assert list.get(1).get("state").equals("NC");
        assert list.get(1).get("areaCode").equals("284");
        assert list.get(2).get("city").equals("Fort Myers");
        assert list.get(2).get("state").equals("FL");
        assert list.get(2).get("areaCode").equals("239");
    }

    @Test
    public void testNonCapturingGroup() {
        assert matches("pass(word)?", "password");
        assert matches("pass(?:word)?", "password");
    }

    @Test
    public void testAtomicGroups() {
        /* Without atomic group.*/
        assert matches("p(ut|u)t", "putt");//matches:  'putt' at 0-4 'putt'

        /* This is also without atomic group. The literal part of the expression 'p' matches input starting part 'p'. Then the left site of the pipe, 'ut' will match the remaining input 'ut'. There's nothing left for the last literal part of the expression 't', so this attempt fails. The engine remembers that there's another part of the alteration so it will backtrack and will try with other alteration 'u' (right side of the pipe). This time it will have a match.*/
        assert matches("p(ut|u)t", "put");//matches:  'put' at 0-3 'put'

        /* Now declare the group as atomic by putting '?>' at the start of the group. Here we don't have a match. That's because, the engine doesn't backtrack for atomic groups. The first 'p' of input string matches the literal part 'p' of the expression. Then 'ut' of the input string matches with alteration part 'ut'. Now nothing left for the last literal 't' of the expression and the the engine won't backtrack (because the group is atomic) to try other alteration. This situation is only valid if engine has a match with current iteration but doesn't have overall match. In other words, The atomic group does not backtrack after the first match of alteration even though there might be an overall match*/
        assert !matches("p(?>ut|u)t", "put");//no matches

        /* . In this example the left alteration doesn't match because 'ut' of the input string doesn't satisfy the expression alteration part 'it' so it will backtrack, even though the group is atomic.*/
        assert matches("p(?>it|u)t", "put");//matches:  'put' at 0-3 'put'

        /* Let's rewrite our very first example, this time making the group atomic. It matches cause we still have one remaining 't' in the input string to match the last literal 't' of the expression*/
        assert matches("p(?>ut|u)t", "putt");//matches:  'putt' at 0-4 'putt'

        /* Atomic groups can have nested capturing group.*/
        assert matches("p(?>(ut|u))\\1", "putut");//matches:  'putut' at 0-5 'putut'
        assert matches("p(?>(ut|u))\\1", "puu");//matches:  'puu' at 0-3 'puu'

        /* Let's start without declaring the group as atomic.*/
        assert matches("<(image|img)>", "<image>");//matches:  '<image>' at 0-7 '<image>'
        assert matches("<(image|img)>", "<img>");//matches:  '<img>' at 0-5 '<img>'

        /* Now declare the group as atomic. The input '<img>' still matches. Because first alteration part 'image' doesn't match at all. Here we have to understand clearly that we are still able to use other alteration 'img' because the current alteration 'image' doesn't match.*/
        assert matches("<(?>image|img)>", "<img>");//matches:  '<img>' at 0-5 '<img>'

        /* In this example the input string has a tag without closing '>' (it might be a valid html construct as it might have some attributes after that point, but we are not interested in that in our example). We are not declaring the group as atomic this time.*/
        assert !matches("<(image|img)>", "<image");//no matches

        /* In above example the overall match failed but the engine had a successful match of 'image' for the first alteration but it couldn't match last '>', so it had to backtrack and to try other alteration 'img'. Isn't that wasteful backtracking for other alteration? Given that first alteration matches and overall match is not successful then the current position of the input string is not satisfying the outside condition (in this example the closing tag '>').Let's make it atomic to avoid useless backtracking and improve some performance.*/
        assert !matches("<(?>image|img)>", "<image");//no matches
    }

    @Test
    public void testBackReference() {
        assert matches("([A-Za-z])[0-9]\\1", "a9a");
        assert matches("[A-Za-z][0-9][A-Za-z]", "a9b");
        assert !matches("([A-Za-z])[0-9]\\1", "a9b");
        assert !matches("\\1[0-9]([A-Za-z])", "a9a");

        /* Find two or more consecutive characters, using backreference.*/
        assert !matches("(\\w)\\1+", "GOVERNESSSHIP  YAYYY EGG HOSTESSHIP");
        assert find("(\\w)\\1+", "GOVERNESSSHIP  YAYYY EGG HOSTESSHIP");

        /* This pattern looks for a group of one or more consecutive characters, followed by the same group of characters.*/
        assert !matches("([a-z]+)\\1", "happiness www banana");
        assert find("([a-z]+)\\1", "happiness www banana");

        /* This pattern looks for a group of one or more consecutive characters, followed by the last character of the same characters sequence. Why last character? That's because the group is repeated multiple times, the quantifier '(....)+' is outside of the group. That means the group 1 will be captured multiple times to have a complete match which includes the backreference \\1 too. The captured group will have only one character each time. On each failure it will discard the last captured character and re-capture the group for the next attempt. As it's also a greedy quantifier it will try to find the longest match, notice the match of 'www' rather than 'ww'. In this case we can imagine ([a-z])+ => ww, last captured value being second w and \\1=>w. In case of 'happiness', ([a-z])+ => happines and \\1 => s*/
        assert find("([a-z])+\\1", "happiness www banana");//matches:  'happiness' at 0-9, 'www' at 10-13

        /* Changing the last example to make the quantifier reluctant by adding ?. Notice the difference this time.*/
        assert find("([a-z])+?\\1", "happiness www banana");
        assert find("(?<myGroup>[A-Za-z])[0-9]\\k<myGroup>", "a9a c0c d68");

        assert find("(?<myGroup>[A-Za-z])[0-9]\\1", "a9a c0c d68");
        assert find("(?<CHAR>(\\w){2,2})[\\S]*\\k<CHAR>", "Cimicic galagala buffalo buffalo");
        assert find("(?<CHAR>(\\w){2,2})[\\S]*\\k<CHAR>", "Cimicic galagala buffalo buffalo");
    }

    @Test
    public void testNormal() {
        assert find("[a-z]+,", "bat, cat, dog, fox");

        assert matches("\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?", "zhangsan@sina.com");
        assert matches("\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?", "zhangsan@xxx.com.cn");

        assert matches("[1-9]\\d{16}[a-zA-Z0-9]{1}", "110110199001183311");

        assert matches("(\\+\\d+)?1[3458]\\d{9}$", "+8613533333333");
        assert matches("(\\+\\d+)?1[3458]\\d{9}$", "13533333333");

        assert matches("(\\+\\d+)?(\\d{3,4}\\-?)?\\d{7,8}$", "+8602085588447");
        assert matches("(\\+\\d+)?(\\d{3,4}\\-?)?\\d{7,8}$", "02085588447");

        assert matches("\\-?[1-9]\\d+", "129312");
        assert matches("\\-?[1-9]\\d+", "-1235253400003243");

        assert matches("\\-?[1-9]\\d+(\\.\\d+)?", "1231414");
        assert matches("\\-?[1-9]\\d+(\\.\\d+)?", "-1231414");
        assert matches("\\-?[1-9]\\d+(\\.\\d+)?", "123.54324");
        assert matches("\\-?[1-9]\\d+(\\.\\d+)?", "-123.54324");

        assert matches("\\s+", " \t\n\r\f");
        assert matches("^[\u4E00-\u9FA5]+$", "哈喽");

        assert matches("[1-9]{4}([-./])\\d{1,2}\\1\\d{1,2}", "1992-09-03");
        assert matches("[1-9]{4}([-./])\\d{1,2}\\1\\d{1,2}", "1992.09.03");
        assert matches("[1-9]{4}([-./])\\d{1,2}\\1\\d{1,2}", "1992/09/03");

        assert matches("(https?://(w{3}\\.)?)?\\w+\\.\\w+(\\.[a-zA-Z]+)*(:\\d{1,5})?(/\\w*)*(\\??(.+=.*)?(&.+=.*)?)?", "http://blog.csdn.net:80/xyang81/article/details/7705960?");
        assert matches("(https?://(w{3}\\.)?)?\\w+\\.\\w+(\\.[a-zA-Z]+)*(:\\d{1,5})?(/\\w*)*(\\??(.+=.*)?(&.+=.*)?)?", "http://www.csdn.net:80");

        assert matches("[1-9]\\d{5}", "471300");

        assert matches("[1-9](\\d{1,2})?\\.(0|([1-9](\\d{1,2})?))\\.(0|([1-9](\\d{1,2})?))\\.(0|([1-9](\\d{1,2})?))", "192.168.1.1");
        assert matches("[1-9](\\d{1,2})?\\.(0|([1-9](\\d{1,2})?))\\.(0|([1-9](\\d{1,2})?))\\.(0|([1-9](\\d{1,2})?))", "127.0.0.1");
    }

    @Test
    public void testSimple() {
        assert matches("a", "a");
        assert matches("b", "b");
        assert !matches("a", "b");

        assert matches("\\n", "\n");
        assert !matches("\\n", "n");
        assert matches("\\t", "\t");
        assert !matches("\\t", "t");
        assert matches("\\(", "(");
        assert matches("\\)", ")");
        assert matches("\\*", "*");
        assert matches("\\+", "+");
        assert matches("\\?", "?");
        assert matches("\\\\", "\\");

        assert matches("abc", "abc");
        assert matches("a\\nb", "a\nb");
        assert !matches("abc", "acb");
        assert !matches("abcdef", "abcde");
        assert find("abcdef", "abcdefg");
    }

    @Test
    public void testCurly() {
        assert matches("a*", "");
        assert matches("a*", "a");
        assert matches("a*", "aaaaaa");
        assert !matches("a*", "aaaabaaa");
        assert find("a*", "aaaabaaa");

        assert find("^a*", "aaaabaaa");
        assert find("a*$", "aaaabaaa");
        assert !find("^a*$", "aaaabaaa");

        assert !matches("a+", "");
        assert matches("a+", "a");
        assert matches("a+", "aaaaaaaaa");
        assert find("a+", "aaaabaaa");
        assert !find("^a+$", "aaaabaaa");

        assert matches("a?", "");
        assert matches("a?", "a");
        assert find("a?", "aa");
        assert find("a?", "ba");
    }

    @Test
    public void testNestedExpressions() {
        assert matches("ab(cd)e", "abcde");
        // with star
        assert matches("a(bc)*d", "ad");
        assert matches("a(bc)*d", "abcd");
        assert matches("a(bc)*d", "abcbcbcd");
        assert !matches("a(bc)*d", "abcbcbd");
        // with plus
        assert !matches("a(bc)+d", "ad");
        assert matches("a(bc)+d", "abcd");
        assert matches("a(bc)+d", "abcbcbcd");
        assert !matches("a(bc)+d", "abcbcbd");
        // with optional
        assert matches("a(bc)?d", "ad");
        assert matches("a(bc)?d", "abcd");
        assert !matches("a(bc)?d", "abcbcd");
    }

    @Test
    public void testOther() {
        assert matches("^\\d+(\\w+)弟等\\d{1,}", "123wwww弟等123");
        assert !matches("^\\d+(\\w+)弟等\\d{1,}", "a123wwww弟等123");
        assert matches("\\d{4}年\\d{1,2}月\\d{1,2}日", "2019年1月12日");

        assert matches("\\Q123\\E", "123");
        assert find("\\Q123\\E", "a123");

        assert matches("\\Q\\w+\\E", "\\w+");

        assert matches("(\\d{2}|\\d){3,}\\d", "012111");
        assert matches("(\\d{2}|\\d){4,}\\d", "012341");
        assert matches("(\\d{2}|\\d){10,}\\d", "012345678912");
        assert !matches("(\\d{2}|\\d)++\\d", "123123121231");

        assert matches("abc$", "abc\r\n");
        assert matches("abc$", "abc\n");
        assert matches("abc$", "abc\r");
        assert matches("abc$", "abc\u0085");
        assert !matches("abc$", "abc\u0088");
        assert !matches("abc$", "abc\n\n");
        assert !matches("abc$", "abc\r\r");
        assert !matches("abc\\z", "abc\r\n");
    }

    @Test
    public void testSpec() {
        assert find("a*+z", "abztstaz");
    }

    @Test
    public void testRef() {
        assert matches("(\\d*)abc\\1", "abc");
        assert !matches("(\\d+)abc\\1", "123abc12");
    }

    private boolean matches(String ptn, String input) {
        return new ReMatcher(new ReTree(ptn), input).matches();
    }

    private boolean find(String ptn, String input) {
        return new ReMatcher(new ReTree(ptn), input).find();
    }

}
