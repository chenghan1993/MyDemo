set serveroutput on
begin
	dbms_output.putline("hello world")
end;
------------------------
declare
	v_name varchar2(25);
begin
	select last_name into v_name from employees where employees_id = 1;
	dbms_output.put_line(v_name);
end;
------------------------
declare
	v_name varchar2(25);
	v_email varchar2(25);
	v_salary number(8,2);
	v_job_id varchar2(10);
begin
	select last_name, email, salary, job_id into v_name, v_email, v_salary, v_job_id
	from employees where employees_id = 1;
	dbms_output.put_line(v_name || ',' || v_email || ',' || v_salary || ',' || v_job_id)
end;
------------------------
declare
	type customer_type is record(
		v_cust_name varchar2(20),
		v_cust_id number(10));
	v_customer_type customer_type;
begin
	v_customer_type.v_cust_name := '刘德华';
	v_customer_type.v_cust_id := 1;
	dbms_output.put_line(v_customer_type.v_cust_name || ',' || v_customer_type.v_cust_id);
end;
------------------------
declare
	type emp_record is record(
		v_name varchar2(25),
		v_email varchar2(25),
		v_salary number(8,2),
		v_job_id varchar2(10));
	v_emp_record emp_record;
begin
	select last_name, email, salary, job_id into v_emp_record from employees where employees_id = 1;
	dbms_output.put_line(v_emp_record.v_name || ',' || v_emp_record.v_eamil || ',' ||
						 v_emp_record.v_salary || ',' || v_emp_record.v_job_id);
end;
------------------------
declare
	type emp_record is record(
	v_name employees.last_name%type,
	v_email employees.email%type,
	v_salary employees.salary%type,
	v_job_id employees.job_id%type);
	
	v_emp_record emp_record;
begin
	select last_name, email, salary, job_id into v_emp_record
	from employees where employees_id = 1;
	dbms_output.put_line(v_emp_record.v_name || ',' || v_emp_record.v_email || ',' ||
						 v_emp_record.v_salary || ',' || v_emp_record.v_job_id);
end;
------------------------
declare
	v_emp_record employees%rowtype;
begin
	select * into v_emp_record from employees where employees_id = 1;
	dbms_output.put_line('...');
end;
------------------------
declare
	v_emp_record employees%rowtype;
	v_employee_id employees.employee_id%type;
begin
	v_employee_id := 1;
	select * from employees where employee_id = v_employee_id;
	dbms_output.put_line('...');
end;
------------------------
declare
	v_emp_id employees.employee_id%type;
begin
	v_emp_id := 1;
	delete from employees where employee_id = v_emp_id;
end;
------------------------
declare 
	v_salary employees.salary%type;
begin
	select salary into v_salary from employees where employee_id = 1;
	dbms_output.put_line('salary: ' || v_salary);
	if v_salary >= 10000 then dbms_output.put_line('salary >= 10000');
	elsif v_salary >= 5000 then dbms_output.put_line('5000 <= salary < 10000');
	else dbms_output.put_line('salary < 5000');
	end if;
end;
------------------------
declare
	v_emp_name employees.last_name%type;
	v_emp_salary employees.salary%type;
	v_emp_sal_level varchar2(20);
begin
	select last_name, salary into v_emp_name, v_emp_salary
	from employees where employee_id = 1;
	if v_emp_salary >= 10000 then v_emp_sal_level := 'salary >= 10000';
	elsif v_emp_salary >= 5000 then v_emp_sal_level := '5000 <= salary < 10000';
	else v_emp_sal_level := 'salary < 5000';
	end if;
	dbms_output.put_line(v_emp_name || ',' || v_emp_salary || ',' || v_emp_sal_level);
end;
------------------------
declare
	v_sal employees.salary%type;
	v_msg varchar2(50);
begin
	select salary into v_sal from employees where employee_id = 1;
	v_msg := 
		case trunc(v_sal/5000)
			when 0 then 'salary < 5000';
			when 1 then '5000 <= salary < 10000';
			else 'salary >= 10000';
		end;
	dbms_output.put_line(v_sal || ',' || v_msg);
end;
------------------------
declare
	v_grade char(1);
	v_job_id employees.job_id%type;
begin
	select job_id into v_job_id from employees where employee_id = 1;
	dbms_output.put_line('v_job_id: ' || v_job_id);
	v_grade := 
		case v_job_id
			when 'IT_PROG' then 'A'
			when 'AC_MGT' then 'B'
			when 'AC_ACCOUNT' then 'C'
			else 'D'
		end;
	dbms_output.put_line('GRADE: ' || v_grade);
end;
------------------------
declare
	v_i number(3) := 1;
begin
	loop
	dbms_output.put_line(v_i);
	exit when v_i = 100;
	v_i := v_i + 1;
	end loop;
end;
------------------------
declare
	v_i number(3) := 1;
begin
	while v_i <= 100 loop
		dbms_output.put_line(v_i);
		v_i := v_i + 1;
	end loop;
end;
------------------------
begin
	for i in 1..100 loop
	dbms_output.put_line(i);
	end loop;
end;
------------------------
declare
	v_flag number(1) := 1;
	v_i number(3) := 2;
	v_j numver(2) := 2;
begin
	while(v_i <= 100) loop
		while v_j <= sqrt(v_i) loop
			if(mod(v_i, v_j) = 0) then v_flag = 0;
			end if;
			v_j = v_j + 1;
		end loop;
		dbms_output.put_line(v_i);
		
		v_flag := 1;
		v_j := 2;
		v_i = v_i + 1;
	end loop;
end;
------------------------
declare
	v_flag number(1) := 0;
begin
	for i in 2..100 loop
		v_flag := 1;
		for j in 2..sqrt(i) loop
			if i mod j = 0 then v_flag = 0; end if;
		end loop
		if v_flag = 1 then dbms_output.put_line(i);
		end if;
	end loop;
end;
------------------------
declare
	v_flag number(1) := 0;
begin
	for i in 2..100 loop
		v_flag = 1;
		for j in 2..sqrt(i) loop
			if i mod j = 0 then v_flag = 0 goto label end if;
		end loop
		<<label>>
		if v_flat = 1 then dbms_output.put_line(i) end if;
	end loop;
end;
------------------------
begin
	for i in 1..100 loop
		dbms_output.put_line(i);
		if (i = 50) then goto label end if;
	end loop;
	<<label>>
	dbms_output.put_line('打印结束');
end;
------------------------
begin
	for i in 1..100 loop
		dbms_output.put_line(i)
		if (i = 50) then
			dbms_output.put_line('打印结束');
			exit;
		end if;
	end loop;
end;
------------------------
declare
	cursor salary_cursor is select salary from employees where employee_id = 1;
	v_salary employees.salary%type;
begin
	open salary_cursor;
	fetch salary_cursor into v_salary;
	while salary_cursor%found loop
		dbms_output.put_line('v_salary: ' || v_salary);
		fetch salary_cursor into v_salary;
	end loop;
	close salary_cursor;
end;
------------------------
declare
	cursor sal_cursor is select salary, last_name from employees where department_id = 1;
	v_sal number(10);
	v_name varchar2(20);
begin
	open sal_cursor;
	fetch sal_cursor into v_sal, v_name;
	while sal_cursor%found loop
		dbms_output.put_line(v_name || '\'s salary is ' || v_sal);
		fetch sal_cursor into v_sal, v_name;
	end loop;
	close sal_cursor;
end;
------------------------
declare
	cursor emp_cursor is select last_name, email, salary from employees where manager_id = 1;
	type emp_record is record(
		v_name employees.last_name%type,
		v_email employees.email%type,
		v_salary employees.salary%type);
	v_emp_record emp_record;
begin
	open emp_cursor;
	fetch emp_cursor into v_emp_record;
	while emp_cursor%found loop
		dbms_output.put_line(v_emp_record.v_name || ',' || v_emp_record.v_email || ',' ||
							 v_emp_record.v_salary);
		fetch emp_cursor into v_emp_record;
	end loop
	close emp_cursor;
end;
------------------------
declare
	cursor emp_cursor is select last_name, email, salary from employees where manager_id = 1;
begin
	for v_emp_record into emp_cursor loop
		dbms_output.put_line(v_emp_record.last_name || ',' || v_emp_record.email || ',' ||
							 v_emp_record.salary);
	end loop;
end;
------------------------
declare
	cursor emp_sal_cursor is select salary, employee_id from employees;
	temp number(4,2);
	v_sal employees.salary%type;
	v_id employees.employee_id%type;
begin
	open cursor;
	fetch emp_sal_cursor into v_sal, v_id;
	while emp_sal_cursor%found loop
		if v_sal <=5000 then temp := 0.05;
		elsif v_sal <= 10000 then temp := 0.03;
		elsif v_sal <= 15000 then temp := 0.02;
		else temp := 0.01
		end if;
		update employee set salary = (1+temp)* salary where employee_id = v_id;
		fetch emp_sal_cursor into v_sal, v_id;
	end loop;
	close emp_sal_cursor;
end;
------------------------
declare
	cursor emp_sal_cursor is select salary, employee_id id from employees;
	temp number(4,2);
begin
	for c in emp_sal_cursor loop
		if c.salary <= 5000 then temp := 0.05;
		elsif c.salary <= 10000 then temp := 0.03;
		elsif c.salary <= 15000 then temp := 0.02;
		else temp := 0.01;
		end if;
		update employees set salary = (1 + temp)*salary where employee_id = c.id;
	end loop
end;
------------------------
declare
	cursor emp_sal_cursor(dept_id number, sal number) is
	select salary + 1000 sal, employee_id id from employees where department_id = dept_id and salary > sal;
	temp number(4,2);
begin
	for c in emp_sal_cursor(sal => 4000, dept_id => 80) loop
		if c.salary <= 5000 then temp := 0.05;
		elsif c.salary <= 10000 then temp := 0.03;
		elsif c.salary <= 15000 then temp := 0.02;
		else temp := 0.01;
		end if;
		update employees set salary = (1+temp) * salary where employee_id = c.id;
	end loop;
end;
------------------------
begin
	update employees set salary = salary + 1000 where employee_id = 1;
	if sal%notfound then
		dbms_output.put_line('查无此人');
	end if;
end;
------------------------
declare
	v_sal employees.salary%type;
begin
	select salary into v_sal from employees where employee_id > 1;
	dbms_output.put_line(v_sal);
	exception
	where Too_many_rows then dbms_output.put_line('输出的行数太多了');
end;
------------------------
declare
	v_sal employees.salary%type;
	delete_mgr_excep exception;
	PRAGMA EXCEPTION_INIT(delete_mgr_excep, -2292);	
begin
	delete from employees where employee_id = 1;
	select salary into v_sal from employees where employees_employee_id = 1;
	dbms_output.put_line(v_sal);
	exception
	where Too_many_rows then dbms_output.put_line('...');
	where delete_mgr_excep then dbms_output.put_line('...');
end;
------------------------
create or replace function func_name(v_name varchar2)
return varchar2
is
begin
	return 'hello world' || v_param
end;
------------------------
create or replace function func_hello
return varchar2
is
begin
	return 'hello world';
end;
begin
	dbms_output.put_line(func_hello());
end;
begin
	select func_hello() from dual;
end;
------------------------
create or replace function func1
return date
is
v_date date
begin
	v_date := sysdate;
	select sysdate into v_date from dual;
	return v_date;
end;
select func1 from dual;
declare
	v_date date;
begin
	v_date := func1;
	dbms_output.put_line('...');
end;
------------------------
create or replace funciton func_add(a number, b number)
return number
is
begin
	return a + b;
end;
begin
	dbms_output.put_line(func_add(1,2));
end;
select func_add(1,2) from dual;
------------------------
create or replace function sum_sal(dept_id number)
return number
is
cursor sal_cursor is select salary from employees where department_id = dept_id;
v_sum_salary number(8) := 0;
begin
	for c in sal_cursor loop
		v_sum_salary := v_sum_salary + c.salary;
	end loop
	return v_sum_salary;
end;
begin
	sum_sal(80);
end;
------------------------
create or replace function sum_sal(dept_id number, total_count out number)
return number
is
cursor sal_cursor is select salary from employees where department_id = dept_id;
v_sum_sal number(8) := 0;
begin
	total_count := 0;
	for c in sal_cursor loop
		v_sum_sal := v_sum_sal + c.salary
		total_count := total_count + 1;
	end loop
	return v_sum_sal;
end;
declare
	v_total number(3) := 0;
begin
	dbms_output.put_line(sum_sal(80, v_total));
	dbms_output.put_line(v_total);
end;
------------------------
create or replace procedure sum_sal_procedure(dept_id number, v_sum_sal out number)
is
cursor sal_cursor is select salary from employees where department_id = dept_id;
begin
	v_sum_sal := 0;
	for c in sal_cursor loop
		v_sum_sal := v_sum_sal + c.salary;
	end loop;
	dbms_output.put_line(v_sum_sal);
end;
declare
	v_sum_sal number(8) := 0;
begin
	sum_sal_procedure(80, v_sum_sal);
end;
------------------------
create or replace procedure add_sal_procedure(dept_id number, temp out number)
is
cursor sal_cursor is select employee_id id, hire_date hd, salary from employees where department_id = dept_id;
a number(4,2) := 0;
begin
	temp := 0;
	for c in sal_cursor loop
		a := 0;
		if c.hd < to_date('1995-1-1','yyyy-mm-dd') then 
			a := 0.05;
		elsif c.hd < to_date('199-1-1','yyyy-mm-dd') then
			a := 0.03;
		else a:= 0.01;
	end if;
		temp := temp + c.salary * a;
		update employees set salary = salary*(1+a) where employee_id = c.id;
	end loop
end;
------------------------
create or replace trigger hello_trigger
after
update on employees;
begin
	dbms_output.put_line('hello world');
end;
------------------------
select last_name, hire_date
from employees
where hire_date = last_day(hire_date) - 1;





































