### 1、泛型类型参数 T 和通配符 ?
```java
protected boolean scanCreate(Class<T> type){
   return type.isAnnotationPresent(Component.class);
}
```
不能直接用 T，原因在于 T 属于泛型类型参数，该方法定义里没有对其进行声明。在 Java 里，若要使用泛型类型参数，就得在方法名之前显式声明。

如下：
```java
protected <T> boolean scanCreate(Class<T> type){
   return type.isAnnotationPresent(Component.class);
}
```
若不需要使用具体的泛型类型参数，使用通配符 ? 来替代 T 是可行的，这样能让代码更简洁。? 表示未知类型，适合在不需要关心具体类型的场景中使用。
```java
protected boolean scanCreate(Class<?> type){
   return type.isAnnotationPresent(Component.class);
}
```
### 2、