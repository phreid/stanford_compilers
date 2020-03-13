class C {
	a : Int <- 1;
	b : Int <- true;
	c : A;
	d : SELF_TYPE <- m1();

	m1() : SELF_TYPE {
		a + b
	};
};

class A inherits C {
		
	init(x : Int) : A {
		{
			a <- x;
			self;
		}	
	};

	m1(x : Int, y : Object) : Int {
		case y of
			z : Int => 0;
			a : String => 1;
			b : Bool => 2;
		esac
	};

	m2(x : Int, y : Bool, z : B) : Int {
		{
			x <- 1;
			y <- true;
			z <- new B;
		}		
	};

	m3() : Object {
		if true then 1 else 2 fi	
	};

	m4() : Object {
		let x : Bool <- true in {
			while x loop
				x
			pool; 
		}
	};
};

class B inherits A {
	m1(x : A) : Int {
		let z : String <- "bye" in {
			z <- "hello";
		}
	};

	m3(x : Int, y : Int) : Int {
		m1(x, y)
	};

	m10(x : Int, y : String) : Bool {
		x = y
	};

	m11(x : Int, y : Int, z : Int) : Int {
		{
			m3(x, y);
			(new A).m4();
			m3(x, y, z);
			m4();
			(new A)@Object.m1();
		}
	};
};