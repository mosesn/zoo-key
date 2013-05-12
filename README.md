## Zoo Key
the unix filesystem-y interface to zookeeper

## motivation
wouldn't it be nice if navigating zookeeper felt like using a terminal?

## api
This is an example of some zoo key code.
```scala
console.mkdir("/foo")
console.mkdir("/foo/bar")
console.cd("/foo")
console.cd("bar")
console.echo("") > "baz"
console.cat("baz")
```

## todo
1. support some flags for some command
2. help command
