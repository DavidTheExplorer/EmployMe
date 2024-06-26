# EmployMe
Ex [Spigot Plugin](https://www.spigotmc.org/resources/employme.96513/) that boosted the economy by allowing players to add their own custom jobs to the Global Jobs Board,\
and pay the player who completed the job.

* **Without** an Economy plugin(e.g. Essentials) EmployMe would shut down.

![A test image](https://i.imgur.com/j7s49wN.png)

## Rewards
Employers can pay either by money, or offer an unlimited amount of valuable items.

## Goals
A job is considered finished once a player reaches the goal item - Which is what the employer needs.\
To do that, the player has to have the goal item in the inventory.

## Ex-Public Wiki
Dear (Potential) Buyer! I wrote concise answers to questions we receive all the time.\
Please search yours here before asking for support on discord :smile:

### General
* **Why would I buy the Premium version? The free is enough for me**\
The free version gives you a taste before buying the plugin :smile: - Eventually you will have issues using it in production.\
It is **significantly** limited: The job board has just 1 page(up to 28 jobs), and the plugin lacks many practical features.\
\
The premium, however, features an Unlimited Board, Discord Webhooks, Support up to the latest spigot version, Partial Job Completions, and way more ways to adapt the plugin to your server!\
The hardest concern is that due to technical difficulties(e.g. re-written configs), Migration to the premium version is impossible.

* **Which currencies does EmployMe support?**\
EmployMe works with Vault - so all famous Economy plugins are supported.\
In fact, EmployMe auto-disables when Vault is not on the server.

* **I bought the Premium version, How to get support?**\
We offer support on our [Discord](https://discord.gg/Tm4F2v7xVE), but you first need to verify your purchase.\
Except for support, you also get the ability to vote on what future updates will include.\
After you join our discord, in the *welcome* channel you will see:\
![image](https://user-images.githubusercontent.com/69223217/224512733-2db02966-20b3-4f70-99a8-1f40748457db.png)\
Simply click the button and continue.

* **I don't understand how to create a Web Hook in the config!**\
Initially you need the URL of the channel you want to send your custom messages to; If you know how to get it, the rest info required is custom and very straightforward to fill.\
If you don't know: Right Click a channel -> Edit Channel -> Integrations -> Create Webhook -> Press "Copy Webhook URL" to get it copied :smile:


* **Why is the plugin heavy?**\
Most of the size comes from [ACF](https://www.spigotmc.org/threads/acf-beta-annotation-command-framework.234266/) and [IF](https://www.spigotmc.org/resources/if-inventory-framework.57216/) that are professional coding frameworks.\
At the cost of a slightly bigger file, EmployMe is painless to maintain.


### In Game
* **I lost money/items while creating a job!**\
You did not lose anything! The reward was transferred back to you :smile:\
You can run _/money_ to verify you got the money back, or _/emp mycontainers_ and then choose _Rewards_ to access your items.

* **I regret creating a job, how to return to the normal chat?**\
Simply say "cancel".

* **The help menu cannot be translated!**\
The help menu is auto-generated by [ACF](https://www.spigotmc.org/threads/acf-beta-annotation-command-framework.234266/) which is a programming library that allows me to create commands very easily.\
I am still looking for a way to make it translatable.

* **Why EmployMe doesn't have other kind of jobs, for example "build me a house" job?**\
This was suggested by many people, and we unfortunately had to reject it because it can't be implemented reliably.\
Imagine a job of _"build me a medieval house"_, How would EmployMe know the house fits the employer's taste? What blocks count as medieval? what would the size of the house be? Too many questions whose answers depend on the employer's desire.\
The only workaround is to let the employer himself declare the job as complete once he reaches an agreement with the employee.\
Now guess what? If people can scam, 99% would scam - the employer can scam the employee by never declaring the job as done.
