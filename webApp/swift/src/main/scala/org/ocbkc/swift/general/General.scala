package org.ocbkc.generic
{

package random
{
import System._
import scala.util.Random

object RandomExtras
{  def nextBetween(ranSeq: Random, min:Int, max:Int):Int = 
   {  min + ranSeq.nextInt( max - min + 1 )
   }

   /** min is inclusive, max is exclusive
     */
   def nextBetween(ranSeq: Random, min:Double, max:Double):Double =
   {  min + ranSeq.nextDouble * (max - min)
   }

   def pickRandomElementFromList[A](list:List[A], rs:Random):Option[A] =
   {  list match
      {  case Nil => None
         case _   => Some(list(rs.nextInt( list.length )))
      }
   }
}
}

object ListUtils
{  def takeNumOrAll[A](list:List[A], num:Int) =
   {  if( num > -1 )
         list.take(num)
      else
         list
   }

   /**  @param f: function which maps element if inList (A) to value of type C, and gets a value of type B as context information originating from the previous time f was applied to the element at the left. 
   *
   */
   // <&y2012.10.23.23:40:37& todo: move to general lib>
   def mapWithLeftContext[A,B,C](inList:List[A], leftContext:B, f:(A,B) => (C,B) ):List[C] =
   {  inList match
      {  case x::xs  => {  val (newX, nextLeftContext) = f(x,leftContext)
                        newX::mapWithLeftContext(xs, nextLeftContext, f)
                     }
         case List() => List()
      }
   }
}

object DateTime
{  import org.ocbkc.swift.global.Types._

   val dateFormat = new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm:ss") // also for reuse!

   def timeInMillis2dateString(time:TimeInMillis) =
   {  dateFormat.format(time).toString
   }
}


/*
//import javax.mail._ <&y2012.06.25.19:45:04& remove this, because I found net.liftweb.util.Mailer.  to do this>
//import javax.mail.internet._


object Mail
{  def send(aFromEmailAddr:String, aToEmailAddr:String, aSubject:String, aBody:String) = 
   {  //Here, no Authenticator argument is used (it is null).
      //Authenticators are used to prompt the user for user
      //name and password.
      val session:Session = Session.getDefaultInstance( fMailServerConfig, null )
      val message = new MimeMessage( session )
      try {
         //the "from" address may be set in code, or set in the
         //config file under "mail.from" ; here, the latter style is used
         //message.setFrom( new InternetAddress(aFromEmailAddr) );
         message.addRecipient(
         Message.RecipientType.TO, new InternetAddress(aToEmailAddr)
         )
         message.setSubject( aSubject )
         message.setText( aBody )
         Transport.send( message )
      }
      catch (ex:MessagingException){
         err.println("Cannot send email. " + ex)
      }
   }
}
*/

/** Package intended to do "coarse parallelism": simulating running function-applications in parallel, just as threads, but then without using threads. This works for function-applications in which there is much "waiting" on other external programs or threads (already existing threads, or threads created elsewhere), and each function application does have to do a lot of work from the perspective of the computer.

Note that the function can only be considered a function, if you also consider the FunAppId as an argument to the function (then it produces a single result for each unique combination of arguments).
@todo &y2015.02.27.22:36:59& investigate whether this can be made really functional by for example also providing a state argument to the start function.
In fact already solved, by also creating a "FunAppId", which can be interpreted as a tacit third argument that is unique for each call of the function ApplicableInParallel.
  */
package coarseParallelism
{
object Types
{  type RequesterCallBackFunctionType = ( => )
}

object TestCoarseParallelism extends ParallelFunAppRequester 
{  def main(args: Array[String]) =
   {  val list = List(1,2,3,4,5,8)
      
      object parallelFunction[]
      {  def start(input:InputType__TP, requester:ParallelFunAppRequester):FunAppId =
         {  log("start( input = " + input.toString)
            // start thread here (normally you assume some external thread to exist or come into existence, but this is for testing purposes.)
            startThread(this)
         }         
      }
   }

   TestThread Thread
   {  
      
      mainThreadThingTODO
      {  sleepTODO random seconds
            
   }
}

trait ApplicableInParallel[InputType__TP, ResultType__TP]
{  val funappRequests:List[FunAppRequest] = Nil

   class FunAppRequest(input:InputType__TP, output:Some[ResultType__TP], requesters:List[RequesterCallBackFunctionType])
   {
   }

   def start(input:InputType__TP, callback: RequesterCallBackFunctionType) =
   {  funappRequests ::= FunApp(input, None)// TODO create FunAppId
   }

   def startBatch(inputList:List[InputType__TP], callback:RequesterCallBackFunctionType) =
   {  funappRequests ++= inputList.map{ FunApp(_, None) }
   }

   /** Call this method as soon as the result is known.
     */
   def finish(result:ResultType__TP) =
   {  requester(input, result, funAppId)
   }
}

trait FunApp[InputType__TP, ResultType__TP](input: InputType__TP)
{  var result: Option[ResultType__TP]
}

trait ParallelFunAppRequester[ResultType__TP]
{  /** @parap input: the original input that was provided when this object called the function ApplicableInParallel.
    */

   def receiveResult(input: InputType__TP, result:ResultType__TP)
   {
   }

   def receiveResultBatch(input: List[Fun], )
   {
   }
}

/** 
  * An instance of this objects forms the connection point between the threads requesting a fnction application and the ones carrying it out. It is connected to a specific object which is ApplicableInParallel. Threads who are intended to deliver results of applications, check this object to see whether there are requests applicable to them, and then deliver them here.
  */


}
