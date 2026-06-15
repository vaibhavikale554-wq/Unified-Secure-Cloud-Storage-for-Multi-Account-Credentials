import requests
import pandas as pd
import json
import os

# API endpoint
API_URL = "https://4bc1-49-15-245-24.ngrok-free.app/save_application"  # Ensure this is correct

# Check and print current working directory
print("Current working directory:", os.getcwd())

# Load CSV data
CSV_FILE = os.path.join(os.getcwd(), "Scripts\data3.csv")

# Ensure file exists before reading
if not os.path.exists(CSV_FILE):
    print(f"Error: The file '{CSV_FILE}' was not found.")
    exit(1)

data = pd.read_csv(CSV_FILE)

# Function to send data to the API
def add_application(app_name, app_url, fields):
    try:
        # Convert fields from string to dictionary
        fields_dict = json.loads(fields.replace("'", '"'))  # Convert to valid JSON format

        # Prepare payload
        payload = {
            "app_name": app_name,
            "app_url": app_url,
            "fields": fields_dict,
        }

        # Make POST request
        response = requests.post(API_URL, json=payload)
        if response.status_code == 200:
            print(f"Successfully added application: {app_name}")
        else:
            print(f"Failed to add application: {app_name}, Error: {response.text}")

    except Exception as e:
        print(f"Error processing application {app_name}: {str(e)}")

# Iterate through CSV data and add applications
for _, row in data.iterrows():
    app_name = row['Application Name']
    app_url = row.get('URL', None)  # Default to None if app_url is missing
    fields = row['Fields']
    print(fields)
    print(f"Processing application: {app_name} with URL: {app_url}")
    add_application(app_name, app_url, fields)
