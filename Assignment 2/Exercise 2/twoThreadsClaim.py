import functools
import os
import threading
from time import perf_counter
from time import sleep
import statistics as stats

# Parametric decorator for multi-threaded benchmarking of a function (fun)
def bench(n_threads:int=1, seq_iter:int=1, iter:int=1):

    # Decorator function to wrap the fun.
    def decorator(fun):
        @functools.wraps(fun)
        def wrapper(*args, **kwargs):
            # Defining the target function assigned to threads, where threads will call the function 'seq_iter' times
            def target():
                for _ in range(seq_iter):
                    fun(*args, **kwargs)
            
            # List to store execution times
            execution_times = []
            
            # Run the experiment for 'iter' times, deploying 'n_threads' threads, each running 
            for _ in range(iter):
                threads = []
                start_time = perf_counter()  # Start time
                
                # Create and start 'n_threads' threads
                for _ in range(n_threads):
                    thread = threading.Thread(target= target)
                    thread.start()
                    threads.append(thread)
                
                # Wait all threads to join
                for thread in threads:
                    thread.join()
                
                # Calculate execution time
                end_time = perf_counter()
                execution_times.append(end_time - start_time)
            
            # Calculate the mean execution time and its variance
            mean_time = stats.mean(execution_times)
            variance_time = stats.variance(execution_times)
            
            # Return the results as a dictionary
            return {
                'fun': fun.__name__,
                'args': args,
                'n_threads': n_threads,
                'seq_iter': seq_iter,
                'iter': iter,
                'mean': mean_time,
                'variance': variance_time
            }
        return wrapper
    return decorator

# Uses 'bench' decorator to evaluate effectiveness of multhitreading in Python
def test(iter, fun, args):
    output_dir = "output"
    # Create the output directory if it doesn't exist
    if not os.path.exists(output_dir):
        os.makedirs(output_dir)
        
    # Define configurations (as required in the specifications) for different combinations of threads and iterations
    configurations = [
        {'n_threads': 1, 'seq_iter': 16},
        {'n_threads': 2, 'seq_iter': 8},
        {'n_threads': 4, 'seq_iter': 4},
        {'n_threads': 8, 'seq_iter': 2}
    ]

    print("performing tests for " + fun.__name__ + " function")
    
    for config in configurations:
        # Apply the bench decorator with the current configuration
        decorated_fun = bench(n_threads=config['n_threads'], seq_iter=config['seq_iter'], iter=iter)(fun)
        
        # Execute the decorated function with the provided arguments
        result = decorated_fun(*args)
        
        # Generate the filename based on function name, args, and configuration
        filename = os.path.join(output_dir, f"{result['fun']}_{result['args']}_{result['n_threads']}_{result['seq_iter']}.txt")
        
        # Write the result to a file
        with open(filename, 'w') as file:
            file.write(str(result))

# CPU Intensive function for testing
def grezzo(n):
    for i in range(2**n):
        pass

# Gets all the primes below the limit (another CPU intensive function)
def get_primes(limit):
    primes = []
    for num in range(2, limit):
        is_prime = True
        for i in range(2, int(num ** 0.5) + 1):
            if num % i == 0:
                is_prime = False
                break
        if is_prime:
            primes.append(num)

# Basic arithmetic operations in a loop + wait function for testing
def mixed_task(n):
    result = 0
    for i in range(n * 1000):
        result += (i * 2) % (n + 1)
    
    sleep(n * 0.05) # NOOP for n/20 seconds

# No operation, just wait function for testing
def just_wait(n): # NOOP for n/10 seconds
    sleep(n * 0.1)

def main():
    test(5, grezzo, (1,))
    #test(5, grezzo, (20,))
    #test(5, get_primes, (100,))
    #test(5, get_primes, (20000,))
    #test(5, get_primes, (100000,))
    #test(5, get_primes, (1000000,))
    #test(5, just_wait, (2,))
    #test(5, mixed_task, (5,))

if __name__ == "__main__":
    main()

# Results (based on a WSL running Ubuntu 22.04)
# Claim to be assessed: “Two threads calling a function may take twice as much time as a single thread calling the function twice”
#
# Let's break down what happens in each function:
#
# 1. grezzo(20) -> 0.232 for 1 thread; 0.233 for 2 threads; 0.231 for 4 threads; 0.235 for 8 threads;
#    Execution time doesn’t change much with more threads (does not scale linearly with the number of threads), 
#    suggesting the GIL (Global Interpreter Lock) in Python limits the benefits of threading for CPU-bound tasks.
#    This is a relatively heavy workload, so more tests are required to draw conclusions.
#
#    grezzo(10) -> 0.00038 for 1 thread; 0.00047 for 2 threads; 0.00073 for 4 threads; 0.00180 for 8 threads;
#    For the lighter workload, execution time slightly increases as threads increase, though not doubling as the claim suggests. 
#    This indicates some overhead introduced by threading as the number of threads grows.
#
#    grezzo(1) -> 0.00016 for 1 thread; 0.00022 for 2 threads; 0.00041 for 4 threads; 0.00084 for 8 threads;
#    As we reduce the workload to a very small task, the claim starts to hold more truth. 
#    The execution time roughly doubles as the number of threads increases.
#
# 2. get_primes(20000) -> 0.259 for 1 thread; 0.270 for 2 threads; 0.264 for 4 threads; 0.270 for 8 threads;
#    get_primes(100000) -> 1.958 for 1 thread; 1.975 for 2 threads; 1.991 for 4 threads; 1.992 for 8 threads;
#    get_primes(1000000) -> 46.775 for 1 thread; 47.172 for 2 threads; 47.459 for 4 threads; 47.265 for 8 threads;
#    As the workload grows even heavier (from 20,000 to 1 million primes), adding threads slightly increases the execution time, 
#    but never doubles it, again showing that heavier CPU-bound tasks do not benefit from threading due to Python's GIL.
#
#    get_primes(100) -> 0.00074 for 1 thread; 0.00081 for 2 threads; 0.00109 for 4 threads; 0.00147 for 8 threads;
#    In the lightweight workload, we observe a small increase in execution time as more threads are added.
#    This implies that threading introduces some overhead without improving performance, particularly in lightweight tasks.
#
# 3. just_wait(2) -> 3.204 for 1 thread; 1.602 for 2 threads; 0.802 for 4 threads; 0.401 for 8 threads;
#    Execution time roughly halves with each doubling of threads, 
#    showing that threading benefits I/O-bound tasks which spend more time waiting 
#    (since they spend much of their time waiting rather than computing).
#
# 4. mixed_task(5) -> 4.012 for 1 thread; 2.006 for 2 threads; 1.004 for 4 threads; 0.505 for 8 threads;
#    Times are halved with more threads due to the I/O-bound nature of the task,
#    so it's consistent with the idea that I/O-bound tasks benefit from parallel execution.
#
# Overall, I/O-bound tasks can benefit from parallel execution, while CPU-bound tasks are limited by the GIL.
# For heavier CPU-bound workloads, adding threads may slightly increase execution time due to overhead, 
# but it doesn't result in the doubling effect suggested by the claim. 
# However, in cases with very lightweight tasks (like grezzo(1)), the claim holds more truth, 
# as the overhead introduced by threading is more noticeable.
#
# Based on the test results, a more accurate claim would be: 
# "For very light workloads, two threads calling a function may take close to twice as much time as a single thread 
#  calling the function twice, but as the workload increases, the additional execution time becomes negligible."