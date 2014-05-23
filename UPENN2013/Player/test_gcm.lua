require('init')
require('gcm')

while 1 do
  print("our score", gcm.get_game_our_score())
  print("our sttate", gcm.get_game_state())
  print("vector of penalites: ", gcm.get_game_penalty())
  print("are we in penalty? ", gcm.in_penalty());
end
