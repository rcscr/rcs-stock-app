# Use an official Node runtime as a parent image
FROM node:14

# Set the working directory in the container
WORKDIR /app

# Copy package.json and package-lock.json to the working directory
COPY package.json ./
COPY package-lock.json ./

# Install the application dependencies
RUN npm install

# Copy the rest of the application code to the working directory
COPY . ./

# Build the React application
RUN npm run build

# Install 'serve' to serve the static files
RUN npm install -g serve

# Expose port 3000 to the outside world
EXPOSE 3000

# Command to run the application
CMD ["serve", "-s", "build", "-l", "3000"]
