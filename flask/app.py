from flask import Flask, request, jsonify
from flask_sqlalchemy import SQLAlchemy
from werkzeug.security import generate_password_hash, check_password_hash
from deepface import DeepFace
import os

app = Flask(__name__)

# Configuring the SQLite database
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///users.db'  # Database location
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False  # Disable modification tracking
db = SQLAlchemy(app)

# Path to store reference images (users' registered images)
REFERENCE_IMAGE_FOLDER = "reference_images"
if not os.path.exists(REFERENCE_IMAGE_FOLDER):
    os.makedirs(REFERENCE_IMAGE_FOLDER)

# User model for the database
class User(db.Model):
    user_id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(150), unique=True, nullable=False)
    full_name = db.Column(db.String(150), nullable=False)
    password = db.Column(db.String(255), nullable=False)

    def __repr__(self):
        return f'<User {self.username}>'

# Adding Application model
class Application(db.Model):
    app_id = db.Column(db.Integer, primary_key=True)
    app_name = db.Column(db.String(150), unique=True, nullable=False)
    app_url = db.Column(db.String(255), nullable=True)
    fields = db.Column(db.JSON, nullable=False)  # Fields stored as JSON

    def __repr__(self):
        return f'<Application {self.app_name}>'

# API to save application details
@app.route('/save_application', methods=['POST'])
def save_application():
    try:
        # Retrieve data from the request
        app_name = request.json.get('app_name')
        app_url = request.json.get('app_url')
        fields = request.json.get('fields')

        if not app_name or not fields:
            return jsonify({"error": "Application name and fields are required"}), 400

        # Check if the application already exists
        existing_app = Application.query.filter_by(app_name=app_name).first()
        if existing_app:
            return jsonify({"error": "Application already exists"}), 400

        # Save application to the database
        new_app = Application(app_name=app_name, app_url=app_url, fields=fields)
        db.session.add(new_app)
        db.session.commit()

        return jsonify({"message": "Application saved successfully"}), 200

    except Exception as e:
        print("Error:", e)
        return jsonify({"error": str(e)}), 500

# API to retrieve all applications
@app.route('/clear_applications', methods=['DELETE'])
def clear_applications():
    try:
        # Delete all entries in the applications table
        db.session.query(Application).delete()
        db.session.commit()
        
        return jsonify({"message": "All applications data has been cleared."}), 200
    except Exception as e:
        db.session.rollback()
        return jsonify({"error": str(e)}), 500
    
    
@app.route('/get_applications', methods=['GET'])
def get_applications():
    try:
        applications = Application.query.all()
        response = [
            {
                "app_name": app.app_name,
                "app_url": app.app_url,
                "fields": app.fields,
            }
            for app in applications
        ]

        return jsonify({"applications": response}), 200

    except Exception as e:
        print("Error:", e)
        return jsonify({"error": str(e)}), 500



# Register endpoint
@app.route('/register', methods=['POST'])
def register_user():
    try:
        
        # User information
        username = request.form.get('username')
        full_name = request.form.get('full_name')
        password = request.form.get('password')
        print(username)

        if not all([username, full_name, password]):
            return jsonify({"error": "Missing required fields"}), 400

        hashed_password = generate_password_hash(password)

        # Validate images
        images = request.files.getlist('images')
        if len(images) != 10:
            return jsonify({"error": "Exactly 10 images are required"}), 400

        # Save user to database
        new_user = User(username=username, full_name=full_name, password=hashed_password)
        db.session.add(new_user)
        db.session.commit()

        user_folder = os.path.join(REFERENCE_IMAGE_FOLDER, str(new_user.user_id))
        os.makedirs(user_folder, exist_ok=True)

        for i, img in enumerate(images):
            img.save(os.path.join(user_folder, f"{i + 1}.jpg"))

        return jsonify({"message": "Registration successful", "username": username}), 200

    except Exception as e:
        print(str(e))
        return jsonify({"error": str(e)}), 500


@app.route('/reset_images', methods=['POST'])
def reset_images():
    try:
        # Get username and validate
        username = request.form.get('username')
        if not username:
            return jsonify({"error": "Username is required"}), 400

        # Fetch user by username
        user = User.query.filter_by(username=username).first()
        if not user:
            return jsonify({"error": "User not found"}), 404

        # Get user_id from user object
        user_id = user.user_id

        # Validate images
        images = request.files.getlist('images')
        if len(images) != 10:
            return jsonify({"error": "Exactly 10 images are required"}), 400

        # Define user folder
        user_folder = os.path.join(REFERENCE_IMAGE_FOLDER, str(user_id))
        if not os.path.exists(user_folder):
            os.makedirs(user_folder)

        # Remove existing images
        for file in os.listdir(user_folder):
            file_path = os.path.join(user_folder, file)
            if os.path.isfile(file_path):
                os.remove(file_path)

        # Save new images
        for i, img in enumerate(images):
            img.save(os.path.join(user_folder, f"{i + 1}.jpg"))

        return jsonify({"message": "Images reset successfully for user", "user_id": user_id}), 200

    except Exception as e:
        print(str(e))
        return jsonify({"error": str(e)}), 500


# Check username
@app.route('/check_username', methods=['POST'])
def check_username():
    try:
        # Get the username from the request
        username = request.form.get('username')

        if not username:
            print("Username is required")
            return jsonify({"status": "failed", "message": "Username is required"}), 400

        # Check if the username already exists in the database
        user_exists = User.query.filter_by(username=username).first()

        if user_exists:
            print("Username already exists")
            return jsonify({"status": "failed", "message": "Username already exists"}), 200
        else:
            print("Username is available")
            return jsonify({"status": "success", "message": "Username is available"}), 200

    except Exception as e:
        print(f"An error occurred: {str(e)}")
        return jsonify({"status": "failed", "message": str(e)}), 500

@app.route('/login', methods=['POST'])
def login_user():
    try:
        # Get the username and password from the request
        username = request.form.get('username')
        password = request.form.get('password')

        if not username or not password:
            print("Both username and password are required")
            return jsonify({"status":"failed","message": "Both username and password are required"}), 400

        # Check if the user exists
        user = User.query.filter_by(username=username).first()
        if not user:
            print("User not registered")
            return jsonify({"status":"failed","message": "Username not found"}), 200

        # Check if the password is correct
        if not check_password_hash(user.password, password):
            print("Invalid password")
            return jsonify({"status":"failed","message": "Invalid password"}), 200

        # Return a success message
        return jsonify({"status":"success","message": "Login successful", "username": username, "full_name": user.full_name}), 200

    except Exception as e:
        return jsonify({"status":"failed","message": str(e)}), 500

REFERENCE_IMAGE_FOLDER = "reference_images"  # Update with your actual path

@app.route('/verify', methods=['POST'])
def verify_faces():
    try:
        print("hiii")
        if 'image' not in request.files:
            print("Image field missing")
            return jsonify({"error": "No image file provided"}), 400

        username = request.form.get('username')
        if not username:
            print("Username missing")
            return jsonify({"error": "Username is required for verification"}), 400

        # Check if an image is sent in the request
        if 'image' not in request.files:
            return jsonify({"error": "No image file provided"}), 400

        # Save the uploaded image
        

        # Retrieve the user from the database
        user = User.query.filter_by(username=username).first()
        if not user:
            print("username not found")
            return jsonify({"error": "User not registered"}), 400
        
        uploaded_image = request.files["image"]
        uploaded_image_path = str(user.user_id)+".jpg"
        uploaded_image.save(uploaded_image_path)
        # Retrieve the reference image paths (stored in user folder based on user_id)
        # user_folder = os.path.join(REFERENCE_IMAGE_FOLDER, str(user.user_id))
        # reference_image_paths = [os.path.join(user_folder, f"{i+1}.jpg") for i in range(10)]

        # Perform face verification using the reference images
        verification_result = None
        # for ref_image_path in reference_image_paths:
        #     print(f"Verifying with {ref_image_path}")
        #     result = DeepFace.verify(
        #         img1_path=ref_image_path,
        #         img2_path=uploaded_image_path,
        #         model_name="VGG-Face",
        #         enforce_detection=False
        #     )
        #     if result["verified"]:
        #         verification_result = result
        #         break
        refrence_img=os.path.join(REFERENCE_IMAGE_FOLDER, str(user.user_id),"1.jpg")
        print(refrence_img)
        result = DeepFace.verify(
                img1_path=refrence_img,
                img2_path=uploaded_image_path,
                model_name="VGG-Face",
                enforce_detection=False
            )
        verification_result = result
        os.remove(uploaded_image_path)
       
        if result["verified"]:
            print("Veriflyed")
            return jsonify({"message": "Face verification successful", "result": verification_result}), 200
        else:
            print("not veriflied")
            return jsonify({"error": "Face verification failed"}), 400
        

    except Exception as e:
        print("Error:", e)
        return jsonify({"error": str(e)}), 500
if __name__ == '__main__':
    # Create the database tables inside an application context
    with app.app_context():
        db.create_all()

    # Run the Flask app
    app.run(host='0.0.0.0', port=5000)
