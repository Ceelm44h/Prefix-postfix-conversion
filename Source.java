import java.util.Scanner;

class Source 
{
	static public Scanner in = new Scanner(System.in);
	
	static Operator[] operators = {new Operator('=', false, 0), 
			new Operator('<', true, 1), new Operator('>', true, 1), 
			new Operator('+', true, 2), new Operator('-', true, 2), 
			new Operator('*', true, 3), new Operator('/', true, 3), new Operator('%', true, 3),
			new Operator('^', false, 4), 
			new Operator('~', false, 5)};

	public static Operator findOperator(char c)
	{
		Operator result = null;
		
		for(Operator op : operators)
		{
			if(c == op.c)
			{
				result = op;
				break;
			}
		}
		return result;
	}
	
	public static String verify(String in, boolean isONP) //check if expression is correct
	{
		String result = "";
		for(char ch : in.toCharArray())
		{
			if(findOperator(ch) != null || 
					((ch == '(' || ch == ')') && !isONP) || 
					(Character.isAlphabetic(ch) && Character.isLowerCase(ch)))
				result += ch;
		}
		return result;
	}
	public static String infToOnp(String inf)
	{
		inf = verify(inf, false);
		Stack<Operator> stack = new Stack<Operator>(inf.length());
		String result = "";
		
		for(int i = 0; i < inf.length(); ++i)
		{ 
			char element = inf.charAt(i);
			Operator selected = findOperator(element);
			
			if (selected != null)  //litera
			{
				if(i == inf.length()-1)
					return "error";
				
				if(selected.c == '~' && i>0 && Character.isAlphabetic(inf.charAt(i-1)))
					return "error";
				
				while(!stack.isEmpty() && ((selected.isLeft && stack.top().priority >= selected.priority) 
						||   (!selected.isLeft
							&& stack.top().priority > selected.priority))  )  
					result += stack.pop().c; 

				stack.push(selected);
				
				if(i>0 && selected.c != '~' && findOperator(inf.charAt(i-1)) != null)
					return "error";
			}  	
			else if (element == '(') 
			{
				if((i!=inf.length()-1 && inf.charAt(i+1) == ')')||(i>0 && inf.charAt(i-1) == ')'))
					return "error";
				
				Operator op = new Operator('(', false, -1);
				stack.push(op);
			}
			else if(element == ')' )  
			{
				while(!stack.isEmpty() && stack.top().c != '(' ) 
					result += stack.pop().c;
				
				if(stack.isEmpty())
					return "error";
				stack.pop();
			}
			else
			{
				result += element;
				
				if(i > 0 && Character.isAlphabetic(inf.charAt(i-1)))
					return "error";
			}
				
		}
		
		while(!stack.isEmpty())
		{
			if(stack.top().c == '(')
				return "error";
			result += stack.pop().c;
		}
							
		if(!onpToInf(result).equals("error"))
			return result;
		else
			return "error";
	}
	
	public static String onpToInf(String onp)
	{
        Stack<String> stack = new Stack<String>(onp.length());
        Stack<Integer> priorityStack = new Stack<Integer>(onp.length());
 
        onp = verify(onp, true);
        String tmp = "";
        int numberOfOperators = 0, numberOfOperands = 0;
 
        for(int i = 0; i < onp.length(); ++i)
        {
        	char ch = onp.charAt(i);
        	
            if(Character.isAlphabetic(ch))
            {
            	stack.push(Character.toString(ch));
                priorityStack.push(6);
                numberOfOperands++;
            }
            else
            {
            	Operator op = findOperator(ch);
            	
                if(ch == '~')
                {
                	
                	if(priorityStack.top() == null)
                		return "error";
                	
                	if(priorityStack.top() <= op.priority && stack.top().length() > 2 && i != 0 && onp.charAt(i-1) != '~')
                		 tmp = ch + "(" + stack.pop() + ")";
                	else
                		 tmp = ch + stack.pop();
 
                	 priorityStack.pop();
                }
                else
                {
                	numberOfOperators++;
                	 if(priorityStack.top() == null)
                    	 return "error";
                	 
                	 if(priorityStack.top() <= op.priority &&
                			 i > 0 && (op.isLeft || Character.isAlphabetic(onp.charAt(i-1)) || findOperator(onp.charAt(i-1)).isLeft))
                		 tmp = "(" + stack.pop() + ")";
                     else
                         tmp = stack.pop();
 
                     priorityStack.pop();
                     
                     if(priorityStack.top() == null)
                    	 return "error";
                     
                     if(priorityStack.top() < op.priority)
                    	 tmp = "(" + stack.pop() + ")" + ch + tmp;
                     else if(priorityStack.top() == op.priority)
                     {
                    	 if(!op.isLeft && (Character.isAlphabetic(onp.charAt(i-1)) || findOperator(onp.charAt(i-1)).isLeft))
                    		 tmp = "(" + stack.pop() + ")" + ch + tmp;
                    	 else
                    		 tmp = stack.pop() + ch + tmp;
                     }
                     else
                    	 tmp = stack.pop() + ch + tmp;
 
                     priorityStack.pop();
                }
            
            
             stack.push(tmp);
             
             if(Character.isAlphabetic(ch))
            	 priorityStack.push(6);   
             else
            	 priorityStack.push(findOperator(ch).priority);
            }
        }
 
        if(numberOfOperators == numberOfOperands - 1)
            return stack.pop();
        else
            return "error";
    }
	
	
	public static void main(String[] args) 
	{

		int n = in.nextInt();
		in.nextLine();
		for(int i = 0; i < n; ++i)
		{
			String test = in.nextLine();
			if(test.substring(0, 3).compareTo("INF") == 0)
				System.out.println("ONP: " + infToOnp(test.substring(5)));
			else
				System.out.println("INF: " + onpToInf(test.substring(5)));
		}
	}
}

class Operator
{
	char c;
	boolean isLeft;
	int priority;
	Operator(char _c, boolean _isLeft, int _priority)
	{
		c = _c;
		isLeft = _isLeft;
		priority = _priority;
	}
}

class Stack<T>
{
	private Object[] arr;
	int top = -1;
	
	public Stack(int size)
	{
		arr = new Object[size];
	}
	
	public void push(T value)
	{
		if(top < arr.length)
		{
			top++;
			arr[top] = value;
		}
	}
	
	public T pop()
	{
		if(!isEmpty())
		{
			T result = top();
			top--;
			return result;
		}
		else
			return null;
	}
	
	@SuppressWarnings("unchecked")
	public T top()
	{
		if(!isEmpty())
			return (T)arr[top];
		else
			return null;
	}
	
	public boolean isEmpty()
	{
		return top == -1;
	}
}