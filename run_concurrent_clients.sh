# Number of clients to run
NUM_CLIENTS=5

# Run clients
for ((i=0; i<$NUM_CLIENTS; i++)); do
    java client.RMIClient localhost 1099 &
done