<template>
  <div class="candidate-card" :class="{ selected: isSelected }" @click="toggleSelect">
    <img :src="candidate.avatar || '/default-avatar.png'" :alt="candidate.name" class="avatar" />
    <div class="info">
      <h3>{{ candidate.name }}</h3>
      <p class="department">{{ candidate.department }}</p>
      <p class="bio">{{ candidate.bio }}</p>
    </div>
    <div class="check-mark" v-if="isSelected">✓</div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  candidate: {
    type: Object,
    required: true
  },
  isSelected: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['toggle'])

const toggleSelect = () => {
  emit('toggle', props.candidate.id)
}
</script>

<style scoped>
.candidate-card {
  display: flex;
  align-items: center;
  padding: 16px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
}

.candidate-card:hover {
  box-shadow: 0 4px 8px rgba(0,0,0,0.15);
}

.candidate-card.selected {
  border: 2px solid #1890ff;
  background: #e6f7ff;
}

.avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  margin-right: 16px;
  object-fit: cover;
}

.info {
  flex: 1;
}

.info h3 {
  margin: 0 0 4px 0;
  font-size: 16px;
}

.department {
  color: #666;
  font-size: 14px;
  margin: 0 0 8px 0;
}

.bio {
  font-size: 13px;
  color: #888;
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.check-mark {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 24px;
  height: 24px;
  background: #1890ff;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
}
</style>
