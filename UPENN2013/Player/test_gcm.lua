require('init')
require('gcm')

while 1 do
  print("our score", gcm.get_game_our_score())
  print("our sttate", gcm.get_game_state())
end
