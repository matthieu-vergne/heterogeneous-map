# `HeterogeneousMap` Definition

A `HeterogeneousMap` allows to map a key to a value with a custom type, as opposed to a `java.util.Map` which maps a key to a value having the type specified for the whole `Map`. Functionally speaking, a `HeterogeneousMap` is equivalent to a `Map` which maps a specific type of keys to `Object` values, but with additional features to have a greater control over the types of values and to ensure type safety.

# Link with `java.util.Map`

Despite the high similarity with `java.util.Map`, the generics do not correspond. Thus, we cannot extend `Map` directly. The `HeterogeneousMap` interface still implements as much as relevant methods from the `Map` interface, to remain close to it, but also the method `toMap()` to translate a `HeterogeneousMap` into a `Map<Key, Object>`.

# Link with Bloch's Typesafe Heterogeneous Containers and Gafter's Super Type Token

People familiar with advanced Java techniques may have heard about the typesafe heterogeneous container of Joshua Bloch (see item 29, p.142 of *Effective Java*, 2nd ed. The Java Series. Upper Saddle River, NJ: Addison-Wesley, 2008. ISBN: 978-0-321-35668-0), which uses directly a `Class<T>` as key to identify a record in a typesafe manner. This implementation, however, does not allow to store several items of the same type, because every `Class<T>` instances are equal, leading to replace the stored item by a new one. Additionally, because there is no dedicated class for reifiable types, like `List<String>` and `List<Integer>`, one cannot have a key with the right type when values need to be stored as such.

Gafter uses instead what he calls [super type tokens](http://gafter.blogspot.com/2006/12/super-type-tokens.html), custom classes which store type custom information. Our `HeterogeneousMap.Key<T>` is such a super type token class: one can create an instance of it with the right, custom type. Super type tokens allow to specify even non-reifiable types. However, because of type erasure, such information is lost at runtime, meaning that it helps during coding without providing full type safety at runtime. Gafter presents [a way](http://gafter.blogspot.com/2007/05/limitation-of-super-type-tokens.html) to store information about the generics, but also how this solution offers partial safety.

In our case, we try to support at best the programmer on these issues by allowing our keys to be generated in two ways:
- with a `Class` instance, for a strict type safety but without reifiable types ;
- without a `Class` instance, for the ability to use reifiable types but with a type safety at best of the compiler's capabilities.

Each key is independent, so some keys can be instantiated with a `Class` and others not for the same `HeterogeneousMap` instance.