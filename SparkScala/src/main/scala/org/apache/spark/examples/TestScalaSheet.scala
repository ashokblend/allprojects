package org.apache.spark.examples

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.ListBuffer
import scala.annotation.tailrec


object TestSparkSqlSheet {
    def main(args:Array[String]){
      //collection
      //partial_function
      //closure()
      //types()
      //currying
      //tailrecursion
      impliciit
    }
    def tailrecursion {
      //https://www.scala-exercises.org/scala_tutorial/tail_recursion
      // tail recursion can be applied only if recursive function calls itself as its last action
      //below is an example of recursive function which calls itself as its last action
      //find gcd of no
      @tailrec
      def gcd(a: Int, b: Int): Int = {
        if (b ==0) {
          a
        } else {
          gcd(b, a%b)
        }
      }
      println(gcd(2,4))
      // as you can see above last action called is itself, let's see other example where its not same case
      def fact(no: Int): Int = {
        if(no ==0) {
          1
        } else {
          no*fact(no-1)
        }
      }
      println(fact(10))
      //as you can see above recursive call , last action is no*fact(no-1), its not tail recursive because last action is not same function call
      //we can rewrite above factiorial function as tail recursive as below
      //as we can see in below method, last action being called is same function
      def fact_tr(no: Int): Int = {
        @tailrec
        def iter(a: Int, result: Int): Int = {
          if(a ==0) {
             result
          } else {
        	  iter(a-1,result*a)
          }
         }
        iter(no,1)
      }

      println(fact_tr(10))
    }

    def impliciit {

      implicit def parsestring(str: String) = {
        new ArrayBuffer[Char] {
          override def length: Int = str.length()
          override def apply(i: Int): Char = str.charAt(i)
        }
      }
      val a =parsestring("ashok")
      println(a.length)
      println("found:"+parsestring("ashok").exists('d'==_))

      //
      val str:String = "testimplicit"
      case class HiString(hi: String) {
        def sayHi {
          println("hi "+hi)
        }
      }
      implicit def teststr(s: String): HiString = HiString(s)
      str.sayHi

      //implicit parameter
      implicit val hiString = HiString("biff")
      def doItnTimes(num: Int)(implicit hiString: HiString) {
        for(i <- 0 to num) {
          println(hiString.sayHi)
        }
      }


    }
    def init[A](fn: =>A):A= {
      println("calling init")
      fn
    }
    def withinit[A](fn: =>A):A = init{
      println("calling withinit")
      fn
    }
    def currying = withinit{
      case class Message(msg: String)
      case class EndPoint(prompt: String) {
        def send(m: Message) {
          println(this.prompt + "" + m.msg)
        }
      }

      def route(msg: Message) = {
        (e: EndPoint) => e.send(msg)
      }
      //Below is one to send message to end point
      val routeciao = route(Message("bye"))
      routeciao(EndPoint("Sending again"))
      routeciao(EndPoint("Sending over again"))
      //Below is other way using currying to send message to endpoint without creating intermdediate function
      route(Message("Bye"))(EndPoint("Sending again"))
      //Now scala support more compact way of doing this. we will rewrite rout function
      def reroute(msg: Message)(endPoint: EndPoint) {
        endPoint.send(msg)
      }
      //Now we can pass in same way without having intermediate val
      reroute(Message("Bye"))(EndPoint("Sending again"))
      // we can do like this also
      val a = reroute(Message("Bye")) _
      //above will return function which can be used to pass endpoint
      a(EndPoint("Sending again"))

      //another example
      def curriedSum(x: Int)(y: Int) = x + y
      println("Curried sum:"+curriedSum(5)(6))
      val c = curriedSum(5) _
      println("Curried sum:"+c(8))
      def cc(a:Int)(b:Int)(c:Int)(d: Int) = a*b*c*d
      val aa=cc(5) _
      val bb=aa(6)
      println("other:"+bb(9)(10))
    }
   /**
    * Traversable -> Iterable ->Seq,Set,Map
    */
    def collection {
       //sequence
       list
    }
    def list {
      val xs = List(1,2,3)
      val ys: List[Any]=xs
      println(s"head ${ys.head} tail:${ys.tail}")
      trait Expr {

      }
      case class Numbers(no: Int) extends Expr
      case class Vars(str: String) extends Expr
      val no = Numbers(10)
      val vars = Vars("ashok")
      val list = no :: Nil
      val varlist = vars :: list
      //updating value of list
      def incAll(xs:List[Int]): List[Int] = xs match {
        case List() =>List()
        case x::xs1 => x+1 :: incAll(xs1)
      }
      val inct = incAll(xs)
      inct.foreach(println)
      // mutable list
      val listbuf = new ListBuffer[Any]
      for(x <- varlist) {
        listbuf +=x
      }
      listbuf.toList.foreach(println)

    }
    /**
     *  Seq -> IndexedSeq, Buffer LinearSeq
     */
    def sequence {
      //array
      arraybuffer
      //indexedSequence
    }
    def arraybuffer {
      val buf = new ArrayBuffer[Int]
      buf +=4
      buf.+=(5)
      buf.+=:(6) //prepend
      buf.++=(buf)
      buf.-(4, 5, 6)
      //buf.foreach(println)
      for (i <- 0 until buf.length) {
        println(buf(i))
      }

      val pf: PartialFunction[Int, String] = {
        case d: Int if d!=0 => "greater then 1"
      }
      val res = buf.collect{
        case i:Int if i>=5 => "greater then 5"
      }
      val res1 = buf.collect(pf)
      res.foreach(println)
      res1.foreach(println)
    }
    def array {
      //Array
      val nos = new Array[Int](2)
      nos(0)=2;nos(1)=2
      nos.foreach(println)
      for(no: Int <- nos) {
        println(no)
      }
      //until doesn't include end
      for (i <- 0 until nos.length) {
        println(nos(i))
      }
      // to -> it includes last
      for (i <- 0 to nos.length-1) {
        println(nos(i))
      }
      //get new array
      val a =nos.++:(nos)
      println("new array length:"+a.length)
      val words = "the quick brown fox".split(" ")
      words.foreach(println)
      //you don't have to define word
      for(word <- words) {
        println(word)
      }
    }
    /**
     * It is for random access of elment which is efficient
     * IndexedSequence -> Array,StringBuilder,Range, String, Vector, ArrayBuffer
     */
    def indexedSequence {
      //Array
     val arr = Array[String]("ashok","Jyoti","Sadhu","Gyanji")
     val arr_noType= Array(1,"ashok")
     val arr_dyn = Array.ofDim[Int](10)
     arr_dyn(0) =1
     (1 to 9).map { x =>
       arr_dyn(x)=x
     }
     //val arr_dyn1 = Array.ofDim(1, 10, 3, 4, 5)    //hell its five dimensional
     range
    }
    def range {
      val r1 = 1 to 10 //op : 1,2,3,4,5,6,7,8,9,10
      val r2 = 1 until 10 //op: 1,2,3,4,5,6,7,8,9
      val r3 = 1 to 10 by 2 // here 2 is step so op: 1,3,5,7,9
      val r4 = 'a' to 'd' // it gives a,b,c :)

      val rr1 = r1.toList
      val rr2 = r2.toArray
    }
    def partial_function {
      val divide = new PartialFunction[Int, Int] { //first int is passed parameter type, second int is result type
        def apply(x: Int)=42/x
        def isDefinedAt(x: Int) = x!=0
      }
      if(divide.isDefinedAt(1)) {
        divide(1)
      }
      //
      val divide1: PartialFunction[Int, Int] = {
        case d: Int if d!=0 => 42/d
      }
      if(divide1.isDefinedAt(1)) {
        divide(1)
      }
      //convert 1 to "one" up to five
      val convertNum2String: PartialFunction[Int, String] = new PartialFunction[Int, String] {
        val nums = Array("One","Two","Three","Four","Five")
        def apply(i: Int) = nums(i-1)
        def isDefinedAt(i: Int) = i>0 && i<=5
      }
      val nums = Array("Six","Seven","Eight","Nine","Ten")
      val convertNum2String1: PartialFunction[Int, String] = {
        case d:Int if d>0 && d<=5 => nums(d-1)
      }
      //partial function chaining
      val handle1to10 = convertNum2String orElse convertNum2String1
      if(handle1to10.isDefinedAt(7)) {
         handle1to10(7)
      }

      if(convertNum2String.isDefinedAt(3)) {
        println(convertNum2String(3))
      }
      //Partial function is used in collection collect method, check its usage for instance as below
      val data = List("ashok",1)
      val res = data collect {
        case str: String => str // this will give output as only "ashok"
      }
      println(res)
      //similary if we use map partial function will not be used for instance
      val n =List(0,1,2)
     // val res1 = n map divide // this will give divide by zero
      //when we use collect it uses partial function and will print 42,21
      val res2 = n collect divide
      println(res2)

      val sample = 1 to 10
      val isOdd: PartialFunction[Int, String] = {
        case no:Int if no%2!=0 => s"$no is odd"
      }
      val isEven: PartialFunction[Int, String] = {
        case no:Int if no%2==0 => s"$no is even"
      }
      val odev = isOdd orElse isEven
      val res3 = sample.collect(odev)
      println(res3)
    }


    //closure start
    def closure() {
      var hello="Hi";
      def sayHello(name:String) {
        println(hello + name)
      }
      exec(sayHello,"ashok")
      hello="ni hao "
      exec(sayHello,"caiqiang")
      hello="sayo"
      exec(sayHello, "Yoshiko")
    }
    def exec(fn:String=>Unit,name:String) {
      fn(name)
    }

    def write[A](obj: A, fname: String) {
      import java.io._
      new ObjectOutputStream(new FileOutputStream(fname)).writeObject(obj)
    }
    class NotSerializable {}
    // apply a generator to create a function with safe decoupled closures

    def closureFunction[E,D,R](enclosed: E)(gen: E => (D => R)) = gen(enclosed)
       class foo() {
       val v1 = 42
       val v2 = 73
       val v3 = 30
       val n = new NotSerializable

       // use shim function to enclose *only* the values of 'v1' and 'v2'
       def f() = closureFunction((v1, v2, v3)) { enclosed =>
         val (v1, v2, v3) = enclosed
         (x: Int) => (v1 + v2 + v3) * x   // Desired function, with 'v1' and 'v2' enclosed
       }
     }
     // This will work!
    val f = new foo().f
    write(f, "/tmp/demo.f")
    //closure end
    def types() {
      type a=(String,Int)
      val v:a=("ashok",2)
    }
}
