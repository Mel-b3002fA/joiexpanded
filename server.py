import os
import logging
from flask import Flask, request, jsonify, render_template
from flask_cors import CORS
import ollama

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Initialize Flask
app = Flask(__name__, template_folder="templates", static_folder="static")
CORS(app)  # Enable CORS if needed

# Set Ollama URL
ollama_url = os.environ.get('OLLAMA_URL')
if not ollama_url:
    raise ValueError("OLLAMA_URL environment variable is not set")
ollama.Client(host=ollama_url)

conversation = []  # Note: Consider stateless storage for conversation

@app.route('/')
def index():
    return render_template('index.html')

@app.route('/chat')
def chat():
    return render_template('chat.html')

@app.route('/about')
def about():
    return render_template('about.html')

@app.route('/contact')
def contact():
    return render_template('contact.html')

@app.route('/tutorial')
def tutorial():
    return render_template('tutorial.html')

@app.route('/chat', methods=['POST'])
def process_chat():
    data = request.get_json()
    if not data or 'message' not in data:
        logger.error("Invalid message received")
        return jsonify({'reply': 'Invalid message'}), 400

    user_message = data['message']
    logger.info(f"User said: {user_message}")
    conversation.append({'role': 'user', 'content': user_message})

    try:
        response = ollama.chat(model='llama3', messages=conversation, timeout=8)
        if 'message' in response and 'content' in response['message']:
            reply = response['message']['content']
            logger.info(f"Joi replied: {reply}")
            conversation.append({'role': 'assistant', 'content': reply})
            return jsonify({'reply': reply})
        else:
            logger.error(f"Unexpected response format: {response}")
            return jsonify({'reply': "Sorry, something went wrong with the model's response."}), 500
    except Exception as e:
        logger.exception(f"Error from Ollama: {e}")
        return jsonify({'reply': "Sorry, something went wrong connecting to the model."}), 500

if __name__ == '__main__':
    port = int(os.environ.get('PORT', 8000))
    app.run(host='0.0.0.0', port=port, debug=False)