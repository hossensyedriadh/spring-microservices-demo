# Spring Reactive Microservices
Implementation of Reactive Microservices using Spring Boot
<hr>
<div>
    This project has 8 microservices:
  <ul>
    <li>
      <strong>Auth Service</strong>
      <section>Responsible for providing authentication using JWTs</section>
    </li>
    <br>
    <li>
      <strong>Discovery Service</strong>
      <section>Responsible for naming and providing information about other microservice instances</section>
    </li>
	<br>
	<li>
      <strong>Mail Service</strong>
      <section>Responsible for consuming emails from Apache Kafka and disbursement using SMTP</section>
	</li>
    <br>
    <li>
      <strong>Edge Service</strong>
      <section>Acts as a Gateway, routing requests to different microservices with authentication (if applicable)</section>
    </li>
    <br>
    <li>
      <strong>Open Service</strong>
      <section>
        Serves APIs that do not require authentication, i.e.: signup, password recovery
      </section>
    </li>
    <br>
    <li>
      <strong>Product Service</strong>
      <section>
        Serves APIs related to products
      </section>
    </li>
    <br>
    <li>
      <strong>Order Service</strong>
      <section>
        Serves APIs related to orders
      </section>
    </li>
    <br>
    <li>
      <strong>User Service</strong>
      <section>
        Serves APIs related to user accounts
      </section>
    </li>
  </ul>
</div>
<br>
<div>
  For synchronous communication, the services use WebClient, and for asynchronous communication, the services use Apache Kafka.
</div>
<hr>
<div>
	<img src="https://github.com/hossensyedriadh/spring-microservices-demo/blob/main/Diagram.png" alt="diagram" width="1080px">
</div>
