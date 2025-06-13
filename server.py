from flask import Flask, request, jsonify, render_template
import ollama

# Set the base URL for Ollama (Make sure Ollama is running on the correct port)
ollama_url = "http://localhost:11434/api/chat"

app = Flask(__name__)
conversation = []

@app.route('/')
def index():
    return render_template('chat.html')

@app.route('/chat', methods=['POST'])
def chat():
    data = request.get_json()

    # Validate input
    if not data or 'message' not in data:
        return jsonify({'reply': 'Invalid message'}), 400

    user_message = data['message']
    print(f"User said: {user_message}")

    # Append user message to conversation
    conversation.append({'role': 'user', 'content': user_message})

    try:
        # Send the conversation history (including user input) to Ollama
        response = ollama.chat(
            model='llama3',
            messages=conversation
        )

        # Check if the response has the expected structure
        if 'message' in response and 'content' in response['message']:
            reply = response['message']['content']
            print(f"Joi replied: {reply}")
            
            # Append AI reply to conversation
            conversation.append({'role': 'assistant', 'content': reply})

            return jsonify({'reply': reply})

        else:
            print("Error: Unexpected response format")
            return jsonify({'reply': "Sorry, something went wrong with the model's response."}), 500

    except Exception as e:
        # Catch errors and return a fallback response
        print("Error from Ollama:", e)
        return jsonify({'reply': "Sorry, something went wrong connecting to the model."}), 500


if __name__ == '__main__':
    app.run(host='127.0.0.1', port=8000, debug=True)
