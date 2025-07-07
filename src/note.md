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
### 2、@PostConstruct 注解和生命周期函数
生命周期函数是在对象创建、使用和销毁等关键阶段自动触发执行的方法，开发者可以在这些方法里编写特定逻辑，以实现初始化、清理等操作。

在 Java 里，@PostConstruct 注解标记的方法会在依赖注入完成之后、对象正式投入使用之前自动调用，这符合生命周期函数在特定阶段执行的特点。具体流程如下： 

对象实例化：通过构造函数创建对象实例。

依赖注入：将对象依赖的其他组件注入到对象中。

@PostConstruct 方法调用：依赖注入完成后，自动调用 @PostConstruct 注解标记的方法。

对象投入使用：完成上述步骤后，对象就可以正常使用了。

参考你当前编辑的代码，postConstructMethod.invoke(bean); 会调用 bean 对象里 @PostConstruct 注解标记的方法，这就是在对象生命周期的特定阶段执行初始化逻辑。

除了 @PostConstruct，Java EE 还提供了 @PreDestroy 注解，用于标记在对象销毁前执行的方法，这也是一种生命周期函数。

```java
@PreDestroy
public void destroy() {
    // 对象销毁前执行清理逻辑
    System.out.println("Object is being destroyed");
}
```
