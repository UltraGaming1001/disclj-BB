(ns routes
  (:gen-class)
  (:require
   [replies               :as r]
   [clojure.string :as string]))

(def empty-mdn
  {:condition (fn [msg event] (and (= "" msg) (re-matches #"(?i)^!mdn.*" (:content event))))
   :result (fn [_ _]  "https://developer.mozilla.org/en-US/docs/Learn_web_development/Core/Scripting")})

(def empty-ns
  {:condition (fn [msg event] (and (= "" msg) (re-matches #"(?i)^!ns.*" (:content event))))
   :result (fn [_ _]  "<https://github.com/bitburner-official/bitburner-src/blob/stable/markdown/bitburner.md>")})

(def too-long
  {:condition (fn [msg _] (< 70 (count msg)))
   :result (fn [_ _]  "Nuh uh, that request is too long.")})

(def robot
  {:condition (fn [msg _] (or (re-matches #"bl?eep ?bl?oop" msg) (re-matches #"bl?oop ?bl?eep" msg)))
   :result (fn [msg _] (string/join " " (-> msg (string/split #"\s") reverse)))})

(def spam
  {:condition (fn [msg _] (= msg "spam"))
   :result (fn [_ _] "https://media.tenor.com/_H10DMOzzzsAAAAM/spam.gif")})

(def yuri
  {:condition (fn [msg _] (= msg "yuri"))
   :result (fn [_ _] "https://muon.blog/botmedia/yuri.png")})

(def poast-coad
  {:condition (fn [msg _] (re-matches #"poast ?coad" msg))
   :result (fn [_ _] "https://muon.blog/botmedia/poastcoad.png")})

(def pspsps
  {:condition (fn [msg _] (re-find #"^ps(ps)+" msg))
   :result (fn [_ _] "https://muon.blog/botmedia/psps.png")})

(def conglats
  {:condition (fn [msg _] (re-find #"^cong(r|l)at(s|u(r|l)ations)?$" msg))
   :result (fn [_ _] "https://muon.blog/botmedia/conglats.gif")})

(def zoe
  {:condition (fn [msg _] (re-matches #"zo[eë]" msg))
   :result (fn [_ _] "https://muon.blog/botmedia/zoe.jpg")})

(def code-format
  {:condition (fn [msg _] (re-matches #"code ?format" msg))
   :result (fn [_ _] "Please format your code so it is readable;

\\`\\`\\`js
like this
\\`\\`\\`

The above gives:
```js
like this
```
Those are backticks ( \\` ), not quotes ( ' ). Found to the left of the 1 key on many keyboards[.](https://muon.blog/botmedia/codeformat.png)")})

(def spoiler-format
  {:condition (fn [msg _] (re-matches #"spoiler( ?format)?" msg))
   :result (fn [_ _] "Please add spoiler tags to spoil-y things;

\\|\\| your spoiler here \\|\\|

The above gives:
|| your spoiler here ||
Those are pipe symbols ( | ). Found above the Enter key on many keyboards, and can be typed via [Shift + \\].")})

(def strike-format
  {:condition (fn [msg _] (re-matches #"strike([ -]?through)?( ?format)?" msg))
   :result (fn [_ _] "To indicate outdated references/comments and avoid confusion, please strikethrough your text;

\\~\\~ outdated comment \\~\\~

The above gives:
~~ outdated comment ~~
Those are tildes ( ~ ). Found to the left of the 1 key on many keyboards, and can be accessed via [Shift + \\`].")})

(def long-code
  {:condition (fn [msg _] (re-matches #"long( ?code)?" msg))
   :result (fn [_ _] "https://muon.blog/botmedia/longcode.png")})

(def static-ram-parsing
  {:condition (fn [msg _] (= msg "attempt"))
   :result (fn [_ _] "-# Originally written by E9Cipher:
# STATIC RAM PARSING

## What it is
Static RAM is the total amount of RAM the game *pre-assigns* to the script before it is executed. For example, if you call `ns.hack()`, the game adds the `hack` function's RAM cost to the script. Every time you call a NetScript function, the game re-checks the static RAM and updates it accordingly.
Note that creating a script incurs a 1.60GB RAM cost by default. This is unavoidable.


## Problems
To see the most common problem with static RAM parsing, let's create a script that hacks n00dles and prints the amount of money it stole to the terminal.
Replace the contents of your newly-made test script with the below code:
```js
/** @param {NS} ns */
export async function main(ns) {
  ns.tprint(await ns.hack(\"n00dles\"));
}
```
As you can see, the RAM cost in the in-game editor is now 1.70GB. This is correct, the `hack` function's RAM cost is 0.10GB, and, added to the base cost of the script (which is 1.60GB, as stated in the previous section), it does indeed sum to 1.70GB.

Now, to store the money we hacked, let's store it in a variable. Name it `stolenMoney`:
```js
  let stolenMoney = await ns.hack(\"n00dles\");
  ns.tprint(stolenMoney);
```
Re-check the static RAM cost of the script. It is still the same cost, 1.70GB, which is correct.

Now, to see the real problem: We know that a hack can be unsuccessful (which can possibly happen when the hacking chance on the server is less than 1, and definitely happens if the server's security is maximum [100] and/or the server's available money is minimum [0]). Therefore, we should name the variable accordingly.
Since we are attempting to hack, let us rename `stolenMoney` to `attempt`:
```js
  // Attempt to hack the n00dles server
  let attempt = await ns.hack(\"n00dles\");
  ns.tprint(attempt);
```
Now, re-check the details of the static RAM cost of the script.
We have the base RAM cost of the script (1.60GB), the `hack` function's cost (0.10GB), and a new `codingcontract.attempt` cost, which is 10GB!
But we didn't call this API, so what happened?


## Why this happened
The static RAM parser is good at what it does - check for NetScript functions and apply the cumulative determined RAM cost to the static RAM cost of the script accordingly. But sometimes, it gives false positives, such as the one we tested, among others.
The keyword `attempt` is part of the `codingcontract` API. The static RAM parser *thinks* we called the API, so it applies the 10GB RAM cost.
But we didn't! We just named a variable!


## Workarounds
The most obvious workaround, as said by hydroflame, is:
> ...that sounds like a lot of effort for very little reward. Name your variables something else.
-# https://discord.com/channels/415207508303544321/415213413745164318/899511549557899295
Note that while `attempt` incurs the API cost, `hackAttempt` doesn't. Thus, adding a prefix/suffix to your variables may be enough for you to avoid it.


## But... how do I know the API keywords?
It may be tedious to check all keywords on a blank script and check the static RAM every time.
Instead, you just need to check whether your variable name turns [blue](https://muon.blog/botmedia/bluekeywords.png) in the in-game editor (or whatever color your syntax.markup is in the Editor Theme).


## And what if I need this keyword?
d0sboots added a specific NetScript function for that: `ns.ramOverride`. If you add it at the beginning of the `main(ns)` function and give it a number (say, 1.70, alluding to our previous example of using `attempt`), it will override the static RAM checker with the parameter as the total RAM cost of the script.
However, a script gains the cumulative dynamic RAM of the functions it has run so far, and if this dynamic RAM exceeds the static RAM of the script, the script will throw an error and stop execution. So, to avoid the error, you have to include the base RAM cost of the script (1.60GB) and the RAM cost of all used NetScript functions, and also update the RAM override whenever you introduce a new function to the script that costs RAM.


## TLDR
Don't use NetScript API keywords to name your own variables.")})

(def endgame-welcome
  {:condition (fn [msg _] (or (re-matches #"endgame( ?welcome)?" msg) (= msg "welcome") (= msg "welcome to endgame")))
   :result (fn [_ event] (if (r/spoil-ok? event) "-# Originally written by FicocelliGuy:
Congratulations on beating the tutorial and welcome to endgame!

There are pins in this channel with recommendations for BitNodes to do early (repeating BitNode 1 to get Source File 1.2 is the strongest bonus, but BitNode 2 unlocks a new mechanic, and BitNode 5 unlocks some nice QoL).

You can change your mind and switch BitNodes at any time if you want, too! There's a new program you unlock to do that, called \"b1t_flum3.exe\"

If you have any questions, feel free to ask!" "To <#415207923506216971> with you!"))})

(def check-pins
  {:condition (fn [msg _] (re-matches #"(check ?(the ?)?)?pins?" msg))
   :result (fn [_ _] "📌 Check the pins!!")})

(def persecution
  {:condition (fn [msg _]
                (or
                 (seq (re-matches #".*(do( not|n'?t|nut|ughnut) buy hacknet).*" msg))
                 (seq (re-matches #".*(hacknet (is( not|n'?t) worth it|sucks|is a bad investment)).*" msg))))
   ;; Source: https://github.com/bitburner-official/typescript-template/issues/23
   :result (fn [_ _] "Hacknet a bad investment early game? You serious? Have you done your mathematics with that thing or not? I guess not. I'm from a heavily mathematical and scientific and scholastic family and lineage since like, Ancient China 5000 years ago? I was forced to do math drills before I could even play anything or whatever. I even had to compete in speed and accuracy with my childhood friend. Of course I won by a mile. Look, calculate the multiplicative aspects of the Hacknets. They'll earn way more than any early servers you can hack because the early game server are so darn poor. hack() is a percentage thingy as far as I can gather. Low server max money, low script income. It doesn't get any simpler to figure out than that. Before you insult my \"mental bandwidth\", I'll have you know that I have full score for all IQ tests I take anytime, anywhere, including those ever increasing difficulty ones that keep going until seemingly forever, and those were so easy and repetitive that I eventually got bored after I passed 300+IQ score and quit out of boredom, even though I originally was aiming for 1000+IQ score. I've always gotten 100% for my Mathematics from kindergarten till end of college/uni and so same programming subjects and logic and abstraction subject. Anyway, enough about me. Just wanted you to know not to judge other people without first getting to know them. It's rude.")})

(def duck
  {:condition (fn [msg _] (or (string/includes? msg "quack") (string/includes? msg "duck")))
   :result (fn [_ _] "quack 🦆")})

(def naughty
  {:condition (fn [msg event]
                (and (not (r/spoil-ok? event)) (some (fn [r] (string/includes? msg r)) r/spoilers)))
   :result (fn [_ _] r/tell-off)})

(def lookup
  {:condition (fn [_ _] true)
   :result (fn [msg event]
             (let [replies (if (string/starts-with? (:content event) "!ns") r/ns-replies r/mdn-replies)]
              ;(prn r/ns-replies)
               (r/signature-decorator
                msg
                event
                (fn [msg event]
                  (if-let [match (-> msg keyword replies :url)]
                    (str "<" match ">")
                    (r/fuzzy-search (r/spoil-ok? event) msg replies))))))})
